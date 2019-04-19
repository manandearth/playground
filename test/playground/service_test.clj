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

#_(def service
  (::http/service-fn (http/create-servlet playground.service/service)))


(def system com.stuartsierra.component.repl/system)
(def service (user/service-fn system))

(defn make-headers
  [username password]
  (let [b64-encoded (base64/encode (str username ":" password))]
    {"Authorization" (str "Basic " (codecs/bytes->str b64-encoded))}))

(defn GET
  [service & opts]
  (apply response-for service :get opts))


#_(GET service "/")

;(assoc :session {:identity {:username "noah"}})
(comment
  (deftest home-test
    (let [;system com.stuartsierra.component.repl/system
                                        ;service (user/service-fn system)
          {:keys [status body]} (response-for service
                                              :get
                                              (url-for :home))]
      (testing "Anonymous user"
        (is (= "<!DOCTYPE html>\n<html><head><title>Home</title></head><div>[ <a href=\"/\">Home</a> | <a href=\"/about\">About</a> | <a href=\"/invoices\">All Entries</a> | <a href=\"/register\">Register</a> | <a href=\"/login\">Login</a> ]</div><div><h1>Hello World!</h1></div></html>"
               (:body (GET service "/")))))
      (testing "Authenticated user"
        (is (= "<!DOCTYPE html>\n<html><head><title>Home</title></head><div>[ <a href=\"/\">Home</a> | <a href=\"/about\">About</a> | <a href=\"/invoices-insert\">Add an entry</a> | <a href=\"/invoices\">All Entries</a> | <a href=\"/logout\">Logout</a> ]</div><div><h1>Hello boo!</h1></div></html>"
               (:body (GET service "/" :headers {:session {:identity {:username "boo"}}}))))))))

#_(:body (assoc (GET service "/") :session {:identity {:username "boo"}}))

#_(deftest greeting-test
  (let [system com.stuartsierra.component.repl/system 
        service (user/service-fn system)
        {:keys [status body]} (response-for service
                                            :get
                                            (url-for :home))] 
    (is (= 200 status))                                        
    (is (= "<!DOCTYPE html>\n<html><head><title>Home</title></head><div>[ <a href=\"/\">Home</a> | <a href=\"/about\">About</a> | <a href=\"/invoices\">All Entries</a> | <a href=\"/register\">Register</a> | <a href=\"/login\">Login</a> ]</div><div><h1>Hello World!</h1></div></html>" body))))

(comment
  (deftest pedestal-example
    (is (= 200 (:status (response-for service
                                      :get (url-for :invoices/:id
                                                    :path-params {:id 159})))))))

(comment
  (deftest login
    (is (= 200 (:status (response-for service
                                      :get (url-for :login)))))))
(comment
  
  (run-tests)
  
  )




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


;;comparing requests

