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
            [playground.server]
            [playground.service]))

(def url-for (route/url-for-routes
              (route/expand-routes playground.service/routes)))

(defmacro with-system
  [[bound-var binding-expr] & body]
  `(let [~bound-var (component/start ~binding-expr)]
     (try
       ~@body
       (finally
         (component/stop ~bound-var)))))

(deftest home-test
  (with-system [sut (user/test-system)]                       
    (let [service               (user/service-fn sut)                 
          {:keys [status body]} (response-for service
                                              :get
                                              (url-for :home))] 
      (is (= 200 status))                                        
      (is (= "<!DOCTYPE html>\n<html><head><title>Home</title></head><div>[ <a href=\"/\">Home</a> | <a href=\"/about\">About</a> | <a href=\"/invoices\">All Entries</a> | <a href=\"/register\">Register</a> | <a href=\"/login\">Login</a> ]</div><div><h1>Hello World!</h1></div></html>" body)))))                           


(deftest pedestal-example
  (with-system [sut (user/test-system)]
    (let [service (user/service-fn sut)
          {:keys [status body]} (response-for service
                                              :get
                                              (url-for :invoices/:id
                                                       :path-params {:id 25}))]
      (is (contains? #{403 404} status))
      (is (.contains body "only permitted to author and admin")))))


(comment
  (run-tests)
  )
