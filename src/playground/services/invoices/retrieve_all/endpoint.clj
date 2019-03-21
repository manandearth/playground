(ns playground.services.invoices.retrieve-all.endpoint
  (:require
   [cheshire.generate]
   [clojure.java.jdbc :as jdbc]
   [honeysql.core :as h]
   [playground.services.invoices.retrieve-all.logic :as logic]
   [playground.views :as views])
  (:import
   [org.postgresql.jdbc4 Jdbc4Array]))

(cheshire.generate/add-encoder Jdbc4Array (fn [c json-generator]
                                            (-> c .getArray (cheshire.generate/encode-seq json-generator))))

(defn perform [{{:keys [id]} :path-params :keys [db] :as request}]
  (let [db     (->> db :pool (hash-map :datasource))
        all-records (->> (logic/query-all)
                         (h/format)
                         (jdbc/query db))]
    {:status 200 :body (views/all-invoices all-records)}))
