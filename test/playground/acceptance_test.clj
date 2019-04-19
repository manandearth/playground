(ns playground.acceptance-test
  (:require [clojure.test :refer :all]
            [playground.test-helper :refer :all]
            #_[sparkledriver.browser :as sd]
            #_[sparkledriver.element :as sde :refer [click! send-text!]]))


#_(use-fixtures :once wrap-test-system wrap-browser)

;; (deftest sign-up-test
;;     (fetch! (app-url "/"))
;;     (click! (find-by-xpath "//a[text()='Login']"))
;;     (is (= (current-path) "/")))
;; _

#_(deftest home
  #_(testing "loading the page"
    (fetch! (app-url "/")))
  (testing "first div tag"
    (is  (= "[ Home | About | All Entries | Register | Login ]\nHello World!"
            (fetch! (app-url "/"))
            (find-by-tag "div")))))
  
(comment
  (deftest basic-tests
    (sd/with-browser [browser (fetch! (sd/make-browser) (str "http:/localhost:" 8080))]
      (testing "loads the page"
        (is (= 200 (sd/status-code browser)))))))






(comment
  (run-tests)
  )

#_(sd/with-browser [browser (fetch! (sd/make-browser)
                                  "http://localhost:8080")]
  (find-by-tag browser "div"))

