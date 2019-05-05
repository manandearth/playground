(ns playground.service-test
  (:require
   [buddy.core.codecs :as codecs]
   [buddy.core.codecs.base64 :as base64]
   [clojure.test :refer :all]
   [com.grzm.component.pedestal :as pedestal-component]
   [com.stuartsierra.component :as component]
   [io.pedestal.http :as http]
   [io.pedestal.http.route :as route]
   [io.pedestal.http.route.definition.table :refer [table-routes]]
   [io.pedestal.test :refer [response-for]]
   [playground.server :as server]
   [playground.service :as service]
   [playground.service :as service]
   [ring.middleware.session.store :as session.store]
   [user]))

(defn test-map [& [session-store]]
  (-> (service/service server/test-http-port) ;; TEST configuration
      (merge {:env                     :test
              ;; do not block thread that starts web server
              ::http/join?           false
              ;; Routes can be a function that resolve routes,
              ;;  we can use this to set the routes to be reloadable
              ::http/routes          #(route/expand-routes (deref #'service/routes))
              ;; all origins are allowed in dev mode
              ::http/allowed-origins {:creds true :allowed-origins any?}
              ;; Content Security Policy (CSP) is mostly turned off in dev mode
              ::http/secure-headers  {:content-security-policy-settings {:object-src "none"}}
              ::http/enable-session (if session-store {:store session-store} nil)})
      http/default-interceptors
      http/dev-interceptors))

(defn env [name]
  {:post [(seq %)]}
  (System/getenv name))

(defn test-system
  [service-map]
  (component/system-map
   :service-map service-map
   ;; :background-processor (background-processor/new :queue-name "cljtest")
   ;; :enqueuer (enqueuer/new :queue-name "cljtest")
   :db (modular.postgres/map->Postgres {:url      "jdbc:postgresql:playground_test"
                                        :user     (env "MANANDEARTH_PLAYGROUND_USER")
                                        :password (env "MANANDEARTH_PLAYGROUND_PASSWORD")})
   :pedestal (component/using (pedestal-component/pedestal (constantly service-map))
                              playground.service/components-to-inject)
   ;:formatting-stack (formatting-stack.component/map->Formatter {})
   ))

(defn test-map+session [username]
  (let [session-store (service/make-session-store (fn [& _]
                                                    {:identity {:username username}})
                                                  (fn [& _]
                                                    {:identity {:username username}})
                                                  (constantly nil))]
    (test-map session-store)))

(def url-for (route/url-for-routes
              (route/expand-routes service/routes)))

(defmacro with-system
  [[bound-var binding-expr] & body]
  `(let [~bound-var (component/start ~binding-expr)]
     (try
       ~@body
       (finally
         (component/stop ~bound-var)))))

(deftest home-test
  (with-system [sut (test-system (test-map))]
    (let [service               (user/service-fn sut)
          {:keys [status body]} (response-for service
                                              :get
                                              (url-for :home))]
      (is (= 200 status))
      (is (= "<!DOCTYPE html>\n<html><head><title>Home</title></head><div>[ <a href=\"/\">Home</a> | <a href=\"/about\">About</a> | <a href=\"/invoices\">All Entries</a> | <a href=\"/register\">Register</a> | <a href=\"/login\">Login</a> ]</div><div><h1>Hello World!</h1></div></html>" body)))))

(deftest update-without-login
  (with-system [sut (test-system (test-map))]
    (let [service (user/service-fn sut)
          {:keys [status body]} (response-for service
                                              :get
                                              (url-for :invoices/:id
                                                       :path-params {:id 25}))]
      (is (contains? #{403 404} status))
      (is (or (.contains body  "only permitted to author and admin")
              (.contains body "Entry not in DB"))))))

(deftest admin-session-test
  (with-system
    [sut (test-system (test-map+session "admin"))]
    (let [service (user/service-fn sut)
          {:keys [status body]} (response-for service
                                              :get
                                              (url-for :home))]
      (is (.contains body "admin")))))

(comment
  (with-system
    [sut (test-system (test-map+session "admin"))]
    (let [service (user/service-fn sut)]
      (response-for service
                    :get
                    (url-for :home)))))

(comment
  (run-tests))

;;A HELPER FOR DEBUGGING:

#_(:body (response-for (make-service-fn (make-session-store (constantly {:identity {:username "admin"}})
                                                            (constantly nil)
                                                            (constantly nil))) :get "/"))
