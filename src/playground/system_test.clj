(ns playground.system-test
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :refer [response-for]]
            [com.stuartsierra.component :as component]
            [clojure.test :refer :all]
            [user]
            [playground.server]
            [playground.service]))

(def url-for (route/url-for-routes
              (route/expand-routes playground.service/routes)))

(defn service-fn
  [system]
  (get-in system [:pedestal :server ::http/service-fn]))

(deftest greeting-test
  (let [system com.stuartsierra.component.repl/system 
        service (service-fn system)
        {:keys [status body]} (response-for service
                                            :get
                                            (url-for :home))] 
    (is (= 200 status))                                        
    (is (= "<!DOCTYPE html>\n<html><head><title>Home</title></head><div>[ <a href=\"/\">Home</a> | <a href=\"/about\">About</a> | <a href=\"/invoices-insert\">Add an entry</a> | <a href=\"/invoices\">All Entries</a> ]</div><div><h1>Hello World!</h1></div></html>" body))))


(defn comp-response-for [verb route]
  (let [system com.stuartsierra.component.repl/system
        service (service-fn system)]
    (response-for service verb route)))

;(comp-response-for :get "/")
