(ns playground.service-test
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :refer [response-for]]
            [io.pedestal.http.route.definition.table :refer [table-routes]]
            [io.pedestal.http :as http]
            [com.stuartsierra.component :as component]
            [clojure.test :refer :all]
            [buddy.core.codecs :as codecs]
            [buddy.core.codecs.base64 :as base64]
            [user]
            [playground.server :as server]
            [playground.service :as service]
            [ring.middleware.session.store :as session.store]
            [playground.service :as service]
            [com.grzm.component.pedestal :as pedestal-component]))

(def test-map
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
              ::http/secure-headers  {:content-security-policy-settings {:object-src "none"}}})
      http/default-interceptors
      http/dev-interceptors))


(defn test-system
  []
  (component/system-map
   :service-map test-map
   ;; :background-processor (background-processor/new :queue-name "cljtest")
   ;; :enqueuer (enqueuer/new :queue-name "cljtest")
   :db (modular.postgres/map->Postgres {:url      (if user/vemv?
                                                    "jdbc:postgresql:ebdb"
                                                    "jdbc:postgresql:postgres_test")
                                        :user     (if user/vemv?
                                                    "root"
                                                    "postgres")
                                        :password (if user/vemv?
                                                    ""
                                                    "postgres")})
   :pedestal (component/using (pedestal-component/pedestal (constantly test-map))
                              playground.service/components-to-inject)
   ;:formatting-stack (formatting-stack.component/map->Formatter {})
   ))

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
  (with-system [sut (test-system)]                       
    (let [service               (user/service-fn sut)                 
          {:keys [status body]} (response-for service
                                              :get
                                              (url-for :home))] 
      (is (= 200 status))                                        
      (is (= "<!DOCTYPE html>\n<html><head><title>Home</title></head><div>[ <a href=\"/\">Home</a> | <a href=\"/about\">About</a> | <a href=\"/invoices\">All Entries</a> | <a href=\"/register\">Register</a> | <a href=\"/login\">Login</a> ]</div><div><h1>Hello World!</h1></div></html>" body)))))                           


(deftest update-without-login
  (with-system [sut (test-system)]
    (let [service (user/service-fn sut)
          {:keys [status body]} (response-for service
                                              :get
                                              (url-for :invoices/:id
                                                       :path-params {:id 25}))]
      (is (contains? #{403 404} status))
      (is (or (.contains body  "only permitted to author and admin")
              (.contains body "Entry not in DB"))))))

;;session

#_(defn make-session-store
  [reader writer deleter]
  (reify session.store/SessionStore
    (read-session [_ k] (reader k))
    (write-session [_ k s] (writer k s))
    (delete-session [_ k] (deleter k))))

(defn make-service-fn
  [session-store]
  (::http/service-fn (http/create-servlet (assoc (service/service server/test-http-port) 
                                                 ::http/enable-session {:store session-store}))))

#_(defn service-fn
  [sys]
  (get-in sys [:pedestal :server ::http/service-fn]))



#_(deftest stub-session-store-test
  (let [expected-id "some-id"
        session-store (make-session-store (constantly {:identity {:username expected-id}})
                                          (constantly nil)
                                          (constantly nil))
        service-fn (make-service-fn session-store)]
    (is (.contains (:body (response-for service-fn :get "/"))  "Exception"))))

(comment
  (run-tests)
  )

#_(:body (response-for (make-service-fn (make-session-store (constantly {:identity {:username "admin"}})
                                                          (constantly nil)
                                                          (constantly nil))) :get "/"))
#_(deftest session-stub
  (with-system [sut (user/test-system)]
    (let [session-store (make-session-store (constantly {:identity {:username "admin"}})
                                            (constantly nil)
                                            (constantly nil))
          service-fn (make-service-fn session-store)
          service (service-fn sut)
          {:keys [status body]} (response-for service
                                              :get
                                              (url-for :invoices/:id
                                                       :path-params {:id 25}))]
      (is (contains? #{403 404} status))
      (is (or (.contains body  "only permitted to author and admin")
              (.contains body "Entry not in DB"))))))
