(ns playground.test-helper
  (:require #_[sparkledriver.browser :as sd]
            #_[sparkledriver.element :as sde]
            [playground.server :as server]
            [playground.service :as service]
            [user]
            #_[booklog.application :as app]
            [com.stuartsierra.component :as component]
            [com.stuartsierra.component.repl :refer [set-init]]
            [clojure.java.io :as io]
            [lambdaisland.uri :as uri]
            #_[booklog.components.spicerack :as sc]))

;; ;;FIXME should perhaps go in a different ns (a sin LambdaIsland example)
;; (defn temp-file-name [name ext]
;;   (str (io/file (System/getProperty "java.io.tmpdir")
;;                 (str name (rand-int 99999) "." ext))))


;; (def test-http-port 59800)

;; (def ^:dynamic *browser* nil )

;; (def browser (sd/make-browser))

;; ;; (sd/fetch! browser "http://localhost:8080")

;; ;; (sd/current-url browser)

;; ;; (sd/page-source browser)

;; ;; (sd/page-text browser)


;; (defn wrap-test-system
;;   "A fixture function which sets up the system before tests and tears it down afterwards."
;;   [tests]
;;   (let [system (component/start user/test-system)]
;;     (tests)
;;     (component/stop user/test-system)
;;     #_(io/delete-file (:db-path config))))
;; (def browser (sd/make-browser))

;; (defn wrap-browser [tests]
;;   "A fixture function which binds *browser* to a new browser instance."
;;   (sd/with-browser [browser (sd/make-browser)]
;;     (binding [*browser* browser]
;;       (tests))))

;; ;;multifunctions for passing a browser instance
;; ;;or it uses implicitly the dynamic *browser* var 
;; (defn fetch!
;;   ([url]
;;    (sd/fetch! *browser* url))
;;   ([browser url]
;;    (sd/fetch! browser url)))

;; (defn find-by-css
;;   ([css]
;;    (sde/find-by-css *browser* css))
;;   ([browser css]
;;    (sde/find-by-css browser css)))

;; (defn find-by-xpath
;;   ([xpath]
;;    (sde/find-by-xpath *browser* xpath))
;;   ([browser xpath]
;;    (sde/find-by-xpath browser xpath)))

;; (defn find-by-tag
;;   ([tag]
;;    (sde/find-by-tag *browser* tag))
;;   ([browser tag]
;;    (sde/find-by-tag browser tag)))

;; (defn page-text
;;   ([]
;;    (sd/page-text *browser*))
;;   ([browser]
;;    (sd/page-text browser)))

;; ;;manipulate urls
;; (defn app-url [path]
;;   (assoc  (uri/uri "http://localhost")
;;           :path path
;;           :port test-http-port))

;; (defn current-path []
;;   (:path (uri/uri (sd/current-url *browser*))))

;; (comment
;;   ;;example:
;;   (sd/with-browser [browser (sd/make-browser)]
;;     (-> (sd/fetch! browser "http://clojure.org")
;;         (sde/find-by-xpath* "//div[@class='clj-intro-message']/p")
;;         (nth 2)
;;         sde/text)))

;; #_(sd/with-browser [browser (sd/make-browser)]
;;   (-> (fetch! browser "http://localhost:8080/")
;;       (sde/find-by-xpath* "//div")))
   

;; #_(sde/click! (find-by-xpath))
;; #_(sde/find-by-xpath "Login")
        

