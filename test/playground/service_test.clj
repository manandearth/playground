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
             (:body (GET service "/" :headers {:session {:identity {:username "boo"}}})))))))

#_(:body (assoc (GET service "/") :session {:identity {:username "boo"}}))

#_(deftest greeting-test
  (let [system com.stuartsierra.component.repl/system 
        service (user/service-fn system)
        {:keys [status body]} (response-for service
                                            :get
                                            (url-for :home))] 
    (is (= 200 status))                                        
    (is (= "<!DOCTYPE html>\n<html><head><title>Home</title></head><div>[ <a href=\"/\">Home</a> | <a href=\"/about\">About</a> | <a href=\"/invoices\">All Entries</a> | <a href=\"/register\">Register</a> | <a href=\"/login\">Login</a> ]</div><div><h1>Hello World!</h1></div></html>" body))))


(deftest pedestal-example
  (is (= 200 (:status (response-for service
                                    :get (url-for :invoices/:id
                                                  :path-params {:id 159}))))))

(deftest login
  (is (= 200 (:status (response-for service
                                    :get (url-for :login))))))
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

(def no-login-request
  {:protocol                              "HTTP/1.1",
   :db  {:url      "jdbc:postgresql:postgres",
                                                                                      :user     "postgres",
                                                                                      :password "postgres",
                                                                                      :pool     [ "com.mchange.v2.c3p0.ComboPooledDataSource[ identityToken -> z8kfsxa29l16ci175669f|7ffecc0b, dataSourceName -> z8kfsxa29l16ci175669f|7ffecc0b ]"]},
   :async-supported?                      true,
   :cookies                               {"ring-session" {:value "AiexH+Oj8MjQ3x5+0QbZ0zPyu9JsY+KA2DPBhNUyWT53mkW1VfbH02jjMvjP0+uFUk7zRrHOKnAH0w6jLsj79A==--lcuWSnMEdsEuvDEDdiu7vVIfu+U5j0hajkA8TmpjwA0="}},
   :remote-addr                           "0:0:0:0:0:0:0:1",
   :flash                                 nil,
   :servlet-response                      [ "HTTP/1.1 200 \nDate: Tue, 16 Apr 2019 20:50:00 GMT\r\n\r\n"],
   :servlet                               [ "io.pedestal.http.servlet.FnServlet@6a723e70"],
   :headers                               {"origin" "", "host" "localhost:8080", "user-agent" "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36", "cookie" "ring-session=AiexH%2BOj8MjQ3x5%2B0QbZ0zPyu9JsY%2BKA2DPBhNUyWT53mkW1VfbH02jjMvjP0%2BuFUk7zRrHOKnAH0w6jLsj79A%3D%3D--lcuWSnMEdsEuvDEDdiu7vVIfu%2BU5j0hajkA8TmpjwA0%3D", "connection" "keep-alive", "upgrade-insecure-requests" "1", "accept" "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8", "accept-language" "en-GB,en-US;q=0.9,en;q=0.8", "accept-encoding" "gzip, deflate, br", "cache-control" "max-age=0"},
   :server-port                           8080,
   :servlet-request                       ["Request(GET //localhost:8080/test)@6e4da7e6"],
   :session/key                           nil,
   :path-info                             "/test",
   :url-for                               {:status :pending, :val nil} ,
   :uri                                   "/test",
   :server-name                           "localhost",
   :query-string                          nil,
   :path-params                           {},
   :body                                  ["HttpInputOverHTTP@61548cc8[c=0,q=0,[0]=null,s=STREAM]"],
   :com.grzm.component.pedestal/component {:db {:url "jdbc:postgresql:postgres", :user "postgres", :password "postgres", :pool ["com.mchange.v2.c3p0.ComboPooledDataSource[ identityToken -> z8kfsxa29l16ci175669f|7ffecc0b, dataSourceName -> z8kfsxa29l16ci175669f|7ffecc0b ]"]}},
   :scheme                                :http,
   :request-method                        :get,
   :session                               {}})



(def login-request
  {:protocol                              "HTTP/1.1",
   :db                                   {:url      "jdbc:postgresql:postgres",
                                                                     :user     "postgres",
                                                                     :password "postgres",
                                                                     :pool     ["com.mchange.v2.c3p0.ComboPooledDataSource[ identityToken -> z8kfsxa29l16ci175669f|7ffecc0b, dataSourceName -> z8kfsxa29l16ci175669f|7ffecc0b ]"]},
   :async-supported?                      true,
   :cookies                               {"ring-session" {:value "atvM/WXPgwbZJFydWVggM4xquCkqQLBxQvp9WPBXTzeIdoFG7NeO5BdRZzRCYuG18+RY5fVCAi/m6gR9M4OiVQ==--OXNR3CzxROHyxNwikcFZ9kdQhixVTiuX5JKZKE1PHgI="}},
   :remote-addr                           "0:0:0:0:0:0:0:1",
   :flash                                 nil,
   :servlet-response                      ["HTTP/1.1 200 \nDate: Tue, 16 Apr 2019 20:58:35 GMT\r\n\r\n"],
   :servlet                               [ "io.pedestal.http.servlet.FnServlet@6a723e70"],
   :headers                               {"origin" "", "host" "localhost:8080", "user-agent" "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36", "cookie" "ring-session=atvM%2FWXPgwbZJFydWVggM4xquCkqQLBxQvp9WPBXTzeIdoFG7NeO5BdRZzRCYuG18%2BRY5fVCAi%2Fm6gR9M4OiVQ%3D%3D--OXNR3CzxROHyxNwikcFZ9kdQhixVTiuX5JKZKE1PHgI%3D", "connection" "keep-alive", "upgrade-insecure-requests" "1", "accept" "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8", "accept-language" "en-GB,en-US;q=0.9,en;q=0.8", "accept-encoding" "gzip, deflate, br"},
   :server-port                           8080,
   :servlet-request                       ["Request(GET //localhost:8080/test)@7173b60b"],
   :session/key                           "atvM/WXPgwbZJFydWVggM4xquCkqQLBxQvp9WPBXTzeIdoFG7NeO5BdRZzRCYuG18+RY5fVCAi/m6gR9M4OiVQ==--OXNR3CzxROHyxNwikcFZ9kdQhixVTiuX5JKZKE1PHgI=",
   :path-info                             "/test",
   :url-for                               [{:status :pending, :val nil}],
   :uri                                   "/test",
   :server-name                           "localhost",
   :query-string                          nil,
   :path-params                           {},
   :body                                  ["HttpInputOverHTTP@304ebc30[c=0,q=0,[0]=null,s=STREAM]"],
   :com.grzm.component.pedestal/component {:db {:url      "jdbc:postgresql:postgres",
                                                                          :user     "postgres",
                                                                          :password "postgres",
                                                                          :pool     ["com.mchange.v2.c3p0.ComboPooledDataSource[ identityToken -> z8kfsxa29l16ci175669f|7ffecc0b, dataSourceName -> z8kfsxa29l16ci175669f|7ffecc0b ]"]}},
   :scheme                                :http,
   :request-method                        :get,
   :session                               {:identity {:username "adam", :role "user"}}})


(= (:headers no-login-request)
   (:headers login-request))
