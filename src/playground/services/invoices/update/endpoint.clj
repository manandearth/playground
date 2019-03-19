(ns playground.services.invoices.update.endpoint
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.spec.alpha :as spec]
   [honeysql.core :as h]
   [playground.services.invoices.update.logic :as logic]))

(spec/def ::amount nat-int?)

(spec/def ::api (spec/keys :req-un [::amount]))

(defn perform [{{:keys [id amount]} :form-params :keys [db] :as request}]
  (let [db     (->> db :pool (hash-map :datasource))
        update (-> (logic/to-update id amount (java.util.UUID/randomUUID))
                   (h/format))
        fetch  (h/format logic/to-query)
        _      (jdbc/execute! db update)
        result (-> (jdbc/query db fetch)
                   (logic/to-serialize))]

    {:status 200
     :body   "OK"}))
