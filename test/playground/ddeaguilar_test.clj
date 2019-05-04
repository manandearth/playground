(ns playground.ddeaguilar-test
  (:require
   [clojure.test :refer :all]
   [io.pedestal.http :as http]
   [io.pedestal.test :refer :all]
   [playground.server :as server]
   [playground.service :as service]
   [ring.middleware.session.store :as session.store]
   [user]))

(def system com.stuartsierra.component.repl/system)
(def service (user/service-fn system))

;; Testing via `response-for` adapted from https://github.com/pedestal/pedestal/blob/09dd88c4ce7f89c7fbb7a398077eb970b3785d2d/service/test/io/pedestal/http/ring_middlewares_test.clj#L181-L212

(defn make-session-store
  [reader writer deleter]
  (reify session.store/SessionStore
    (read-session [_ k] (reader k))
    (write-session [_ k s] (writer k s))
    (delete-session [_ k] (deleter k))))

(defn make-service-fn
  [session-store]
  (::http/service-fn (http/create-servlet (assoc service/service
                                                 ::http/enable-session {:store session-store}))))

(deftest stub-session-store-test
  (let [expected-id "boo"
        session-store (make-session-store (constantly {:id expected-id})
                                          (constantly nil)
                                          (constantly nil))
        service-fn (make-service-fn session-store)]
    (is (= expected-id (:body (response-for service-fn :get "/test"))))))

(def boo-session (make-session-store (constantly {:identity {:username "boo"}})
                                     (constantly nil)
                                     (constantly nil)))

(defn make-comp-session
  [session-store]
  (::http/service-fn (http/create-servlet (assoc server/dev-map
                                                 ::http/enable-session {:store session-store}))))

(response-for (make-comp-session boo-session) :get "/")

(comment

  (run-tests))

(make-service-fn boo-session)
