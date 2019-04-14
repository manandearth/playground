(ns playground.services.invoices.delete.endpoint
  (:require
   [clojure.spec.alpha :as spec]
   [clojure.java.jdbc :as jdbc]
   [honeysql.core :as h]
   [buddy.auth :refer [authenticated? throw-unauthorized]]
   [io.pedestal.http.route :refer (url-for)]
   [playground.services.invoices.delete.logic :as logic]
   [playground.services.invoices.retrieve-all.logic :as retrieve-all.logic]
   [playground.models.user :as models.user]
   [ring.util.response :as ring-resp]))

(spec/def ::api (spec/keys :req-un [::models.user/id]))

(defn perform [{{:keys [id]} :path-params :keys [db session] :as request}]
  (let  [role (get-in session [:identity :role])]
    (if (= "admin" role)
      (let [db (->> db :pool (hash-map :datasource))
            _  (->> (logic/to-delete id)
                    (h/format)
                    (jdbc/execute! db))
            fetch  (h/format (retrieve-all.logic/query-all))
            left   (->> (logic/to-serialize)
                        (h/format)
                        (jdbc/query db)
                        (first))
            result (jdbc/query db fetch)
            result-map {:result result :deleted id}]
        (-> (ring-resp/redirect (url-for :invoices))
            (assoc :flash (str "Entry " id " has beed deleted."))))
      (throw-unauthorized))))
