(ns playground.services.invoices.retrieve.endpoint
  (:require
   [cheshire.generate]
   [clojure.java.jdbc :as jdbc]
   [clojure.spec.alpha :as spec]
   [honeysql.core :as h]
   [ring.util.response :as ring-resp]
   [io.pedestal.http.route :refer [url-for]]
   [playground.services.invoices.retrieve.logic :as logic]
   [playground.services.invoices.retrieve.view :as view]
   [playground.models.user :as models.user]
   [buddy.auth :refer [authenticated? throw-unauthorized]]
   [playground.views :as views])
  (:import
   [org.postgresql.jdbc4 Jdbc4Array]))

(cheshire.generate/add-encoder Jdbc4Array (fn [c json-generator]
                                            (-> c .getArray (cheshire.generate/encode-seq json-generator))))

(spec/def ::api (spec/keys :req-un [::models.user/id]))

(defn perform [{{:keys [id]} :path-params :keys [session db] :as request}]
  (if (authenticated? session)
      (let [db     (->> db :pool (hash-map :datasource))
            record (->> (logic/to-query id)
                        (h/format)
                        (jdbc/query db)
                        (first))]
        (if record
          (ring-resp/response (view/update-invoice record))
          {:status 404 :body "Entry not in DB"}))
      (-> (ring-resp/redirect (url-for :invoices))
          (assoc :flash "you must login to edit any entries"))
      ))
