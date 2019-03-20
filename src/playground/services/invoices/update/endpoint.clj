(ns playground.services.invoices.update.endpoint
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.spec.alpha :as spec]
   [honeysql.core :as h]
   [playground.services.invoices.update.logic :as logic]
   [playground.services.invoices.retrieve-all.logic :as retrieve-all.logic]))

(spec/def ::amount nat-int?)

(spec/def ::api (spec/keys :req-un [::amount]))

(defn perform [{{:keys [id amount]} :form-params :keys [db] :as request}]
  (let [db     (->> db :pool (hash-map :datasource))
        update (-> (logic/to-update id amount (java.util.UUID/randomUUID))
                   (h/format))
        fetch  (h/format (retrieve-all.logic/query-all))
        _      (jdbc/execute! db update)
        result (jdbc/query db fetch)]
    
    {:result result :confirmation (str "The entry " id " has been updated.")}))
