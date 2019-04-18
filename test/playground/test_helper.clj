(ns playground.test-helper
  (:require [sparkledriver.browser :as sd]
            [sparkledriver.element :as sde]
            [playground.server :as server]
            [playground.service :as service]
            [user]
            #_[booklog.application :as app]
            [com.stuartsierra.component :as component]
            [com.stuartsierra.component.repl :refer [set-init]]
            [clojure.java.io :as io]
            [lambdaisland.uri :as uri]
            #_[booklog.components.spicerack :as sc]))

;;FIXME should perhaps go in a different ns (a sin LambdaIsland example)
(defn temp-file-name [name ext]
  (str (io/file (System/getProperty "java.io.tmpdir")
                (str name (rand-int 99999) "." ext))))


(def test-http-port 59800)

(def ^:dynamic *browser* nil )

(def browser (sd/make-browser))

(sd/fetch! browser "http://localhost:8080")

(sd/current-url browser)

(sd/page-source browser)

(sd/page-text browser)







(defn test-config []
  {:http-port test-http-port
   :db-path (temp-file-name "test" "db")})

(defn rehardened-reset []
  (do
    (clojure.core/require 'clojure.tools.namespace.repl)
    (clojure.core/require 'com.stuartsierra.component.repl)
    ((clojure.core/resolve 'clojure.tools.namespace.repl/set-refresh-dirs) "src" "test")
    (try
      ((clojure.core/resolve 'com.stuartsierra.component.repl/reset))
      (catch java.lang.Throwable v
        (clojure.core/when (clojure.core/instance? java.io.FileNotFoundException v)
          ((clojure.core/resolve 'clojure.tools.namespace.repl/clear)))
        (clojure.core/when ((clojure.core/resolve 'com.stuartsierra.component/ex-component?) v)
          (clojure.core/let [stop (clojure.core/resolve 'com.stuartsierra.component/stop)]
            (clojure.core/some-> v clojure.core/ex-data :system stop)))
        (throw v)))))



(defn wrap-test-system
  "A fixture function which sets up the system before tests and tears it down afterwards."
  [tests]
  (let [config (test-config)
        system (user/dev-system)]
     (set-init (fn [_]
                     (user/dev-system)))
    (rehardened-reset)
    (tests)
    (component/stop system)
    #_(io/delete-file (:db-path config)))) 

(defn wrap-browser [tests]
  "A fixture function which binds *browser* to a new browser instance."
  (sd/with-browser [browser (sd/make-browser)]
    (binding [*browser* browser]
      (tests))))

;;multifunctions for passing a browser instance
;;or it uses implicitly the dynamic *browser* var 
(defn fetch!
  ([url]
   (sd/fetch! *browser* url))
  ([browser url]
   (sd/fetch! browser url)))

(defn find-by-css
  ([css]
   (sde/find-by-css *browser* css))
  ([browser css]
   (sde/find-by-css browser css)))

(defn find-by-xpath
  ([xpath]
   (sde/find-by-xpath *browser* xpath))
  ([browser xpath]
   (sde/find-by-xpath browser xpath)))

(defn page-text
  ([]
   (sd/page-text *browser*))
  ([browser]
   (sd/page-text browser)))

;;manipulate urls
(defn app-url [path]
  (assoc  (uri/uri "http://localhost")
          :path path
          :port test-http-port))

(defn current-path []
  (:path (uri/uri (sd/current-url *browser*))))


(sd/with-browser [browser (sd/make-browser)]
  (-> (sd/fetch! browser "http://clojure.org")
      (sde/find-by-xpath* "//div[@class='clj-intro-message']/p")
      (nth 2)
      sde/text))
