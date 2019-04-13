(ns playground.services.invoices.delete.endpoint
  (:require
   [clojure.spec.alpha :as spec]
   [clojure.java.jdbc :as jdbc]
   [honeysql.core :as h]
   [playground.services.invoices.delete.logic :as logic]
   [playground.services.invoices.retrieve-all.logic :as retrieve-all.logic]
   [playground.models.user :as models.user]))

(spec/def ::api (spec/keys :req-un [::models.user/id]))

(defn perform [{{:keys [id]} :path-params :keys [db] :as request}]
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
    {:status 302 :headers {"Location" "/invoices"} :body "" :flash (str "Entry " id " has beed deleted.")}))
