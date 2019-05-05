(ns playground.services.invoices.delete.endpoint
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.spec.alpha :as spec]
   [honeysql.core :as h]
   [io.pedestal.http.route :refer [url-for]]
   [playground.models.user :as models.user]
   [playground.services.invoices.delete.logic :as logic]
   [ring.util.response :as ring-resp]))

(spec/def ::api (spec/keys :req-un [::models.user/id]))

(defn perform [{{:keys [id]} :path-params :keys [db session] :as request}]
  (let [db (->> db :pool (hash-map :datasource))]
    (->> (logic/to-delete id)
         (h/format)
         (jdbc/execute! db))
    (-> (ring-resp/redirect (url-for :invoices))
        (assoc :flash (str "Entry " id " has beed deleted.")))))
