(ns playground.service-test
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :refer [response-for]]
            [io.pedestal.http.route.definition.table :refer [table-routes]]
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


(defn print-routes
  "Print our application's routes"
  []
  (route/print-routes (table-routes playground.service/routes)))

(defn named-route
  "Finds a route by name"
  [route-name]
  (->> playground.service/routes
       table-routes
       (filter #(= route-name (:route-name %)))
       first))

(defn print-route
  "Prints a route and its interceptors"
  [rname]
  (letfn [(joined-by
            [s coll]
            (apply str (interpose s coll)))

          (repeat-str
            [s n]
            (apply str (repeat n s)))

          (interceptor-info
            [i]
            (let [iname  (or (:name i) "<handler>")
                  stages (joined-by
                          ","
                          (keys
                           (filter
                            (comp (complement nil?) val)
                            (dissoc i :name))))]
              (str iname " (" stages ")")))]
    (when-let [rte (named-route rname)]
      (let [{:keys [path method route-name interceptors]} rte
            name-line (str "[" method " " path " " route-name "]")]
        (joined-by
         "\n"
         (into [name-line (repeat-str "-" (count name-line))]
               (map interceptor-info interceptors)))))))





#_(deftest home-page-test
  (is (= (-> service (response-for :get "/") :body)
         "Hello World!"))
  (is (= (-> service (response-for :get "/") :headers)
         {"Content-Type" "text/html;charset=UTF-8"
          "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
          "X-Frame-Options" "DENY"
          "X-Content-Type-Options" "nosniff"
          "X-XSS-Protection" "1; mode=block"
          "X-Download-Options" "noopen"
          "X-Permitted-Cross-Domain-Policies" "none"
          "Content-Security-Policy" "object-src 'none'; script-src 'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:;"})))

#_(deftest about-page-test
  (is (-> service (response-for :get "/about") :body (.contains "Clojure 1.8")))
  (is (= (-> service (response-for :get "/about") :headers)
         {"Content-Type" "text/html;charset=UTF-8"
          "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
          "X-Frame-Options" "DENY"
          "X-Content-Type-Options" "nosniff"
          "X-XSS-Protection" "1; mode=block"
          "X-Download-Options" "noopen"
          "X-Permitted-Cross-Domain-Policies" "none"
          "Content-Security-Policy" "object-src 'none'; script-src 'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:;"})))

