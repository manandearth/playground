(ns playground.server
  (:gen-class)
  (:require
   [com.stuartsierra.component :as component]
   [io.pedestal.http :as server]
   [io.pedestal.http.route :as route]
   [io.pedestal.http.route.definition.table :refer [table-routes]]
   [playground.service :as service]))

(def dev-map
  (-> service/service ;; start with production configuration
      (merge {:env                     :dev
              ;; do not block thread that starts web server
              ::server/join?           false
              ;; Routes can be a function that resolve routes,
              ;;  we can use this to set the routes to be reloadable
              ::server/routes          #(route/expand-routes (deref #'service/routes))
              ;; all origins are allowed in dev mode
              ::server/allowed-origins {:creds true :allowed-origins any?}
              ;; Content Security Policy (CSP) is mostly turned off in dev mode
              ::server/secure-headers  {:content-security-policy-settings {:object-src "none"}}})
      server/default-interceptors
      server/dev-interceptors))

(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nCreating your server...")
  (-> service/service server/create-server server/start))

(defn test?
  [service-map]
  (-> service-map :env #{:test}))

(defrecord Pedestal [service-map service]
  component/Lifecycle
  (start [this]
    (if service
      this
      (cond-> service-map
        true                      server/create-server
        (not (test? service-map)) server/start
        true                      ((partial assoc this :service)))))

  (stop [this]
    (when (and service (not (test? service-map)))
      (server/stop service))
    (dissoc this :service)))

(defn print-routes
  "Print our application's routes"
  []
  (route/print-routes (table-routes service/routes)))

(defn named-route
  "Finds a route by name"
  [route-name]
  (->> service/routes
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
