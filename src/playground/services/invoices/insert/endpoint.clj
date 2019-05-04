(ns playground.services.invoices.insert.endpoint
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.spec.alpha :as spec]
   [honeysql.core :as h]
   [playground.models.user :as models.user]
   [playground.services.invoices.insert.logic :as logic]))

(spec/def ::api (spec/keys :req-un [::models.user/amount]))

(defn perform [{{:keys [amount]} :form-params :keys [db session] :as request}]
  (let [db     (->> db :pool (hash-map :datasource))
        insert (-> (logic/to-insert amount
                                    (java.util.UUID/randomUUID)
                                    (get-in session [:identity :username]))
                   (h/format))
        _      (jdbc/execute! db insert)]
    {:status 301 :headers {"Location" "/invoices"} :body "" :flash "Inserted to DB"}))
