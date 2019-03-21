(ns playground.services.invoices.insert.endpoint
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.spec.alpha :as spec]
   [honeysql.core :as h]
   [playground.services.invoices.insert.logic :as logic]
   [playground.services.invoices.retrieve-all.logic :as retrieve-all.logic]
   [playground.views :as views]))

(spec/def ::amount nat-int?)

(spec/def ::api (spec/keys :req-un [::amount]))

(defn perform [{{:keys [amount]} :form-params :keys [db] :as request}]
  (let [db     (->> db :pool (hash-map :datasource))
        insert (-> (logic/to-insert amount (java.util.UUID/randomUUID))
                   (h/format))
        fetch  (h/format logic/to-query)
        fetch-all (h/format (retrieve-all.logic/query-all))
        _      (jdbc/execute! db insert)
        result (-> (jdbc/query db fetch)
                   (logic/to-serialize))
        result-all (-> (jdbc/query db fetch-all))
        result-map {:result result-all
                    :confirmation (str "The entry " (:id result) " has been inserted.")}]

    {:status 200 :body (views/submit-invoice result-map)}))
