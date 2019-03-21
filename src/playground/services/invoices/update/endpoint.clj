(ns playground.services.invoices.update.endpoint
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.spec.alpha :as spec]
   [honeysql.core :as h]
   [playground.services.invoices.update.logic :as logic]
   [playground.services.invoices.retrieve-all.logic :as retrieve-all.logic]
   [playground.services.invoices.update.view :as view]
   [clojure.string :as string]))

(spec/def ::name (spec/and string? (complement string/blank?)))

(spec/def ::api (spec/keys :req-un [::name]))

(defn perform [{{:keys [name]} :form-params {:keys [id]} :path-params :keys [db] :as request}]
  (let [db     (->> db :pool (hash-map :datasource))
        update (-> (logic/to-update id name (java.util.UUID/randomUUID))
                   (h/format))
        fetch  (h/format (retrieve-all.logic/query-all))
        _      (jdbc/execute! db update)
        result (jdbc/query db fetch)
        result-map {:result result :confirmation (str "The entry " id " has been updated.")}]

    {:status 200 :body (view/submit-invoice result-map)}))
