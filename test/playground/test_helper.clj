(ns playground.test-helper
  (:require  [clojure.test :refer :all]
             [playground.server :as server]
             [playground.service :as service]
             [user]
             [com.stuartsierra.component :as component]
             [com.stuartsierra.component.repl :refer [start stop set-init]]
             [clojure.java.io :as io]
             [etaoin.api :refer :all]
             [etaoin.keys :as k]
             ))

;;manipulate urls

(def ^:dynamic *driver*)

(def test-port 59800)

(def app-port 8080)

(defn test-url [path]
  (str "http://localhost:" test-port path))

(defn app-url [path]
  (str "http://localhost:" app-port path))

(def test-sys (user/test-system))

(use-fixtures
  :once (fn [tests]
          (try
            (alter-var-root #'test-sys component/start)
            (with-chrome-headless nil  driver
              (binding [*driver* driver]
                (tests)))
            (finally
              (alter-var-root #'test-sys component/stop)))))

(deftest home
  (testing "register element without session"
    (is (= clojure.lang.Atom (type (with-chrome-headless nil driver
                   (doto driver
                     (go (test-url "/"))
                     (has-text? "Hello")
                     )))))
    (is (= "Home" (with-chrome-headless nil driver
                                (go driver (test-url "/"))
             (get-title driver))))))

(deftest admin-login
  (testing "Log-in as admin"
    (is (= true
           (with-chrome-headless nil driver
             (doto driver
               (go (test-url "/"))
               (click {:tag :a :fn/has-text "Login"})
               (fill {:tag :input :name :username} "admin")
               (fill {:tag :input :name :password} "admin")
               (click {:tag :input :type :submit})
               )
             (has-text? driver "Hello admin!")
             ))))
  (testing "Add an entry as admin"
    (is (= true
           (with-chrome-headless nil driver
             (doto driver
               (go (test-url "/login"))
               (fill {:tag :input :name :username} "admin")
               (fill {:tag :input :name :password} "admin")
               (click {:tag :input :type :submit})
               (go (app-url "/invoices-insert"))
               (fill {:tag :input :name :amount} "666")
               (click {:tag :input :type :submit}))
             (has-text? driver "666@"))))))


(comment
  (run-tests)
  )

(comment
  ;;BUILDING A TEST
  (def driver (chrome))
  (go driver (app-url "/login"))
  (fill driver {:tag :input :name :username} "noah")
  (fill driver {:tag :input :name :password} "conoy")
  (click driver {:tag :input :type :submit})
  (go driver (app-url "/invoices-insert"))
  (fill driver {:tag :input :name :amount} "666")
  (click driver {:tag :input :type :submit})
  (has-text? driver "666@")
  )

