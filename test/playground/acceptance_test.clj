(ns playground.acceptance-test
  (:require [clojure.test :refer :all]
            [playground.test-helper :refer :all]
            [sparkledriver.browser :as sd]
            [sparkledriver.element :as sde :refer [click! send-text!]]))


(use-fixtures :once wrap-test-system wrap-browser)

#_(deftest sign-up-test
    (fetch! (app-url "/"))
    (click! (find-by-xpath "//a[text()='Login']"))
    (is (= (current-path) "/")))

(deftest home
  (fetch! (app-url "/"))
  (println (page-text)))

(comment
  (run-tests)
  )
