(ns playground.services.invoices.update.endpoint
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.spec.alpha :as spec]
   [honeysql.core :as h]
   [playground.services.invoices.update.logic :as logic]
   [playground.services.invoices.retrieve-all.logic :as retrieve-all.logic]
   [clojure.string :as string]))

(spec/def ::name (spec/and string? (complement string/blank?)))

(spec/def ::api (spec/keys :req-un [::name]))

(defn perform [{{:keys [name]} :form-params {:keys [id]} :path-params :keys [db] :as request}]
  (let [db     (->> db :pool (hash-map :datasource))
        update (-> (logic/to-update (Integer/parseInt id) name (java.util.UUID/randomUUID))
                   (h/format))
        _      (jdbc/execute! db update)]

    {:status 302 :headers {"Location" "/invoices"} :body ""}))
