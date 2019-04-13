(ns playground.services.invoices.update.endpoint
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.spec.alpha :as spec]
   [honeysql.core :as h]
   [io.pedestal.http.route :refer [url-for]]
   [playground.services.invoices.update.logic :as logic]
   [playground.services.invoices.retrieve-all.logic :as retrieve-all.logic]
   [ring.util.response :as ring-resp]
   [clojure.string :as string]))

(spec/def ::name (spec/and string? seq (complement string/blank?)))

(spec/def ::api (spec/keys :req-un [::name]))

(defn perform [{{:keys [name]} :form-params {:keys [id]} :path-params :keys [db] :as request}]
  (let [db     (->> db :pool (hash-map :datasource))
        update (-> (logic/to-update (Integer/parseInt id) name (java.util.UUID/randomUUID))
                   (h/format))
        _      (jdbc/execute! db update)]


    (-> (ring-resp/redirect (url-for :invoices))
        (assoc :flash (str "Entry " id " has been updated")))))

