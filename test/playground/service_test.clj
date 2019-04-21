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

(def system
  ;;com.stuartsierra.component.repl/system
  user/test-system)
(def service (user/service-fn system))

(deftest pedestal-example
  (testing
      "update an entry without login"
      (is (= (or 403 404) (:status (response-for service
                                                 :get (url-for :invoices/:id
                                                               :path-params {:id 155})))))))


#_(deftest login
  (is (= 200 (:status (response-for service
                                    :get (url-for :login))))))

#_(deftest home-page-test
  (is (= (-> service (response-for :get "/") :body)
         "<!DOCTYPE html>\n<html><head><title>Home</title></head><div>[ <a href=\"/\">Home</a> | <a href=\"/about\">About</a> | <a href=\"/invoices\">All Entries</a> | <a href=\"/register\">Register</a> | <a href=\"/login\">Login</a> ]</div><div><h1>Hello World!</h1></div></html>"))
  )

#_(deftest about-page-test
  (is (-> service (response-for :get "/about") :body (.contains "Clojure 1.10.0")))
  )

(comment
  (run-tests)
  )



