(ns playground.browser-test-helper
  (:require  [clojure.test :refer :all]
             [playground.server :as server]
             [playground.service :as service]
             [user]
             [com.stuartsierra.component :as component]
             [com.stuartsierra.component.repl :refer [start stop set-init]]
             [clojure.java.io :as io]
             [lambdaisland.uri :as uri]
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


(defn wrap-test-system
  "A fixture function which sets up the system before tests and tears it down afterwards."
  [tests]
  (let [system (component/start user/test-system)]
    (tests)
    (component/stop user/test-system)
    #_(io/delete-file (:db-path config))))

(defn fixture-driver
  "Executes a test running a driver. Bounds a driver
   with the global *driver* variable."
  [f]
  (with-chrome {:headless true} driver
    (binding [*driver* driver]
      (f))))
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

(comment
  (use-fixtures
  :once (fn [tests]
          (let [test-sys (make-system ...)]
            (try
              (component/start test-sys)
              (with-chrome-headless nil  driver
                (binding [*driver* driver]
                  (tests)))
              (finally
                (component/stop test-sys)))))))

#_(def driver (chrome))
;; (has-text? driver "Register")

;;(def driver (chrome-headless))

;;(quit driver)

;(component/update-system (user/test-system))

(deftest home
  (testing "register element without session"
    (is (= clojure.lang.Atom (type (with-chrome-headless nil driver
                   (doto driver
                     (go (test-url "/"))
                     (has-text? "Hello")
                     )))))
    (is (= "Home" (with-chrome-headless nil driver
                                (go driver (test-url "/"))
             (get-title driver)))))
  )

(deftest check
  (testing "right"
    (is (= 4 (+ 2 2))))
  (testing "wrong.."
    (is (= 5 (+ 2 2)))))


(comment
  (run-tests)
  )

(comment

  ;; let's perform a quick Wiki session

  (go driver "https://en.wikipedia.org/")
  (wait-visible driver [{:id :simpleSearch} {:tag :input :name :search}])

  ;; search for something
  (fill driver {:tag :input :name :search} "Clojure programming language")
  (fill driver {:tag :input :name :search} k/enter)
  (wait-visible driver {:class :mw-search-results})

  ;; I'm sure the first link is what I was looking for
  (click driver [{:class :mw-search-results} {:class :mw-search-result-heading} {:tag :a}])
  (wait-visible driver {:id :firstHeading})

  ;; let's ensure
  (get-url driver) ;; "https://en.wikipedia.org/wiki/Clojure"

  (get-title driver) ;; "Clojure - Wikipedia"

  (has-text? driver "Clojure") ;; true

  ;; navigate on history
  (back driver)
  (forward driver)
  (refresh driver)
  (get-title driver) ;; "Clojure - Wikipedia"

  ;; stops Firefox and HTTP server
  (quit driver)

  ;; You see, any function requires a driver instance as the first argument. So you may simplify it using doto macros:

  #_(def driver (chrome)))


(comment
  (doto driver
    (go "https://en.wikipedia.org/")
    (wait-visible [{:id :simpleSearch} {:tag :input :name :search}])
    ;; ...
    (fill {:tag :input :name :search} k/enter)
    (wait-visible {:class :mw-search-results})
    (click :some-button)
    ;; ...
    (wait-visible {:id :firstHeading})
    ;; ...
    (quit)))


