(ns playground.services.session.register.endpoint
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.spec.alpha :as spec]
            [honeysql.core :as h]
            [ring.util.response :as ring-resp]
            [io.pedestal.http.route :refer [url-for]]
            [playground.services.session.register.logic :as logic]))


(spec/def ::username (spec/and string? seq (complement clojure.string/blank?)))
(spec/def ::password (spec/and string? seq (complement clojure.string/blank?)))
(spec/def ::api (spec/keys :req-un [::username ::password]))

(defn perform  [{{:keys [username password]} :form-params :keys [db] :as request}]
  (let [db     (->> db :pool (hash-map :datasource))
        query (-> (logic/to-check username)
                  (h/format))
        check (jdbc/query db query)
        insert (-> (logic/to-insert username
                                    (logic/derive-password password))
                   (h/format))]
    (if (empty? check)
      (do (jdbc/execute! db insert)
          (-> (ring-resp/redirect (url-for :login))
              (assoc :flash (str  username ", you are registered. please login"))))
      (-> (ring-resp/redirect (url-for :register))
          (assoc :flash "username already taken. choose another")))))
