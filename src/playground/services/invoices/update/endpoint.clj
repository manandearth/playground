(ns playground.services.invoices.update.endpoint
  (:require
   [clojure.java.jdbc :as jdbc]
   [clojure.spec.alpha :as spec]
   [honeysql.core :as h]
   [io.pedestal.http.route :refer [url-for]]
   [playground.models.user :as models.user]
   [playground.services.invoices.update.logic :as logic]
   [ring.util.response :as ring-resp]))

(spec/def ::api (spec/keys :req-un [::models.user/name]))

(defn perform [{{:keys [name]} :form-params {:keys [id]} :path-params :keys [db session] :as request}]
  (let [db     (->> db :pool (hash-map :datasource))
        update (-> (logic/to-update (Integer/parseInt id) name (java.util.UUID/randomUUID))
                   (h/format))
        _      (jdbc/execute! db update)]

    (-> (ring-resp/redirect (url-for :invoices))
        (assoc :flash (str "Entry " id " has been updated")))))
