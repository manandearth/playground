(ns user
  "Tools for interactive development with the REPL. This file should
  not be included in a production build of the application.

  Call `(reset)` to reload modified code and (re)start the system.

  The system under development is `system`, referred from
  `com.stuartsierra.component.repl/system`.

  See also https://github.com/stuartsierra/component.repl"
  (:require
   [clojure.java.io :as io]
   [clojure.pprint :refer [pprint]]
   [clojure.set :as set]
   [clojure.string :as string]
   [com.grzm.component.pedestal :as pedestal-component]
   [com.stuartsierra.component :as component]
   [com.stuartsierra.component.repl :refer [reset set-init system]]
   [io.pedestal.http :as http]
   [io.pedestal.http.route :as route]
   [io.pedestal.http.route.definition.table :refer [table-routes]]
   [io.pedestal.test :refer [response-for]]
   [modular.postgres]
   [playground.server]
   [playground.service]))

;; ugly hack, will disappear when we use the newer project template
(def vemv? (-> (System/getenv "USER") #{"vemv" "victor.valenzuela"}))

(defn dev-system
  []
  (component/system-map
   :service-map playground.server/dev-map
   ;; :background-processor (background-processor/new :queue-name "cljtest")
   ;; :enqueuer (enqueuer/new :queue-name "cljtest")
   :db (modular.postgres/map->Postgres {:url      (if vemv?
                                                    "jdbc:postgresql:ebdb"
                                                    "jdbc:postgresql:playground_dev")
                                        :user     (if vemv?
                                                    "root"
                                                    "postgres")
                                        :password (if vemv?
                                                    ""
                                                    "postgres")})
   :pedestal (component/using (pedestal-component/pedestal (constantly playground.server/dev-map))
                              playground.service/components-to-inject)
   ;:formatting-stack (formatting-stack.component/map->Formatter {})
   ))

(set-init (fn [_]
            (dev-system)))

;;response-for urls

(defn service-fn
  [sys]
  (get-in sys [:pedestal :server ::http/service-fn]))

(defn response-map-for-route [verb route]
  (let [service (service-fn system)]
    (response-for service verb route)))

;;printing routes and interceptors

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
