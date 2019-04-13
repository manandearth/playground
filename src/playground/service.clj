(ns playground.service
  (:require
   [clojure.core.async :as async :refer [>! go]]
   [clojure.spec.alpha :as spec]
   [com.grzm.component.pedestal :as pedestal-component]
   [io.pedestal.http :as http]
   [io.pedestal.http.body-params :as body-params]
   [io.pedestal.http.route :as route]
   [io.pedestal.interceptor :as interceptor]
   [io.pedestal.interceptor.chain :as interceptor-chain]
   [io.pedestal.http.ring-middlewares :as ring-middlewares]
   [playground.coerce :as coerce]
   [playground.jobs.sample]
   [playground.services.invoices.delete.endpoint :as invoices.delete]
   [playground.services.invoices.insert.endpoint :as invoices.insert]
   [playground.services.invoices.retrieve-all.endpoint :as invoices.retrieve-all]
   [playground.services.invoices.retrieve.endpoint :as invoices.retrieve]
   [playground.services.invoices.update.endpoint :as invoices.update]
   [playground.services.session.register.endpoint :as session.register]
   [playground.services.session.login.endpoint :as session.login]
   [playground.views :as views]
   [ring.util.response :as ring-resp]
   [ring.middleware.session.cookie :as cookie]
   [ring.middleware.flash :as flash]
   [buddy.auth.middleware :refer [authentication-request]]
   [buddy.auth.backends.session :refer [session-backend]]))

(defn about-page [request]
  (ring-resp/response (views/about request)))

(defn home-page [request]
  (ring-resp/response (views/home request)))

(defn insert-page [request]
  (ring-resp/response (views/insert request)))

(defn register-page [request]
  (ring-resp/response (views/register request)))

(defn login-page [request]
  (ring-resp/response (views/login request)))

(defn logout [request]
  (-> (ring-resp/redirect (route/url-for :login))
      (assoc-in [:session :identity] nil)
      (assoc :flash "You have logged out")))

(spec/def ::temperature int?)

(spec/def ::orientation (spec/and keyword? #{:north :south :east :west}))

(spec/def ::api (spec/keys :req-un [::temperature ::orientation]))

(defn api [{{:keys [temperature orientation]} :query-params :keys [db] :as request}]
  #_(go
      (-> enqueuer :channel (>! (playground.jobs.sample/new temperature))))
  {:status 200
   :body   {:temperature temperature :orientation orientation}})

;;auth interceptors

(def session-auth-backend
  (session-backend
   {:authfn (fn [request]
              (let [{:keys [username password]} request]
                (when (= (session.login/password-by-username username) password)
                  {:username username :password password})))}))

(def authentication-interceptor
  "Port of buddy-auth's wrap-authentication middleware."
  (interceptor/interceptor
   {:name ::authenticate
    :enter (fn [context]
             (update context :request authentication-request session-auth-backend))}))


;;;;;;;;;;;;;;;;;;;
(defn param-spec-interceptor
  "Coerces params according to a spec. If invalid, aborts the interceptor-chain with 422, explaining the issue."
  [spec params-key]
  {:name  ::param-spec-interceptor
   :enter (fn [context]
            (let [result (coerce/coerce-map-indicating-invalidity spec (get-in context [:request params-key]))]
              (if (contains? result ::coerce/invalid?)
                (-> context
                    (assoc :response {:status 422
                                      :body   {:explanation (spec/explain-str spec result)}})
                    interceptor-chain/terminate)
                (assoc-in context [:request params-key] result))))})

(defn context-injector [components]
  {:enter (fn [{:keys [request] :as context}]
            (reduce (fn [v component]
                      (assoc-in v [:request component] (pedestal-component/use-component request component)))
                    context
                    components))
   :name  ::context-injector})

(def components-to-inject [:db
                           #_:background-processor #_:enqueuer])

(def component-interceptors
  (conj (mapv pedestal-component/using-component components-to-inject)
        (context-injector components-to-inject)))

(def session-interceptor (ring-middlewares/session {:store (cookie/cookie-store)}))

(def flash-interceptor (ring-middlewares/flash))

(def common-interceptors (into component-interceptors [(body-params/body-params) http/html-body authentication-interceptor session-interceptor flash-interceptor]))

(def routes
  "Tabular routes"
  #{["/" :get (conj common-interceptors `home-page) :route-name :home]
    ["/about" :get (conj common-interceptors `about-page)]
    ["/api" :get (into component-interceptors [http/json-body (param-spec-interceptor ::api :query-params) `api])]
    ;;FIXME change the routes definition format from: (def routes #{...})
    ;;to (def routes (io.pedestal.http.route.definition.table/table-routes ...))
    ;;as "/invoices/:id" is conflicting with "/invoices/insert"
    ["/register" :get (conj common-interceptors  `register-page) :route-name :register]
    ["/register" :post (into common-interceptors [http/json-body (param-spec-interceptor ::session.register/api :form-params) `session.register/perform])]
    ["/login" :get (conj common-interceptors `login-page) :route-name :login]
    ["/login" :post (into common-interceptors [http/json-body (param-spec-interceptor ::session.login/api :form-params) `session.login/perform])]
    ["/logout" :get (conj common-interceptors `logout)]
    ["/invoices-insert" :get (conj common-interceptors `insert-page)]
    ["/invoices-insert" :post (into common-interceptors [http/json-body (param-spec-interceptor ::invoices.insert/api :form-params) `invoices.insert/perform])]
    ["/invoices-update/:id" :post (into common-interceptors [http/json-body (param-spec-interceptor ::invoices.update/api :form-params) `invoices.update/perform])]
    ["/invoices/:id" :get (conj common-interceptors (param-spec-interceptor ::invoices.retrieve/api :path-params) `invoices.retrieve/perform) :route-name :invoices/:id]
    ["/invoices" :get (conj common-interceptors `invoices.retrieve-all/perform) :route-name :invoices]
    ["/invoices-delete/:id" :get (into common-interceptors [http/json-body (param-spec-interceptor ::invoices.delete/api :path-params) `invoices.delete/perform]) :route-name :invoices-delete/:id]
    })

(comment
  (def routes
    "Map-based routes"
    `{"/" {:interceptors [(body-params/body-params) http/html-body]
           :get          home-page
           "/about"      {:get about-page}}})
  (def routes
    "Terse/Vector-based routes"
    `[[["/" {:get home-page}
        ^:interceptors [(body-params/body-params) http/html-body]
        ["/about" {:get about-page}]]]]))

;; Consumed by playground.server/create-server
;; See http/default-interceptors for additional options you can configure
(def service
  {:env                     :prod
   ;; You can bring your own non-default interceptors. Make
   ;; sure you include routing and set it up right for
   ;; dev-mode. If you do, many other keys for configuring
   ;; default interceptors will be ignored.
   ;; ::http/interceptors []
   ::http/routes            routes
   ;; Uncomment next line to enable CORS support, add
   ;; string(s) specifying scheme, host and port for
   ;; allowed source(s):
   ;;
   ;; "http://localhost:8080"
   ;;
   ;; ::http/allowed-origins ["scheme://host:port"]

   ;; Tune the Secure Headers
   ;; and specifically the Content Security Policy appropriate to your service/application
   ;; For more information, see: https://content-security-policy.com/
   ;;   See also: https://github.com/pedestal/pedestal/issues/499
   ;; ::http/secure-headers {:content-security-policy-settings {:object-src "'none'"
   ;; :script-src "'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:"
   ;; :frame-ancestors "'none'"}}

   ;; Root for resource interceptor that is available by default.
   ::http/resource-path     "/public"

   ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
   ;; This can also be your own chain provider/server-fn -- http://pedestal.io/reference/architecture-overview#_chain_provider
   ::http/type              :jetty
   ;; ::http/host "localhost"
   ::http/port              8080
   ;; Options to pass to the container (Jetty)
   ::http/container-options {:h2c? true
                             :h2?  false
                             ;; :keystore "test/hp/keystore.jks"
                             ;; :key-password "password"
                             ;; :ssl-port 8443
                             :ssl? false}})
