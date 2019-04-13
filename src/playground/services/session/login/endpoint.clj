(ns playground.services.session.login.endpoint
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core :as h]
            [clojure.spec.alpha :as spec]
            [ring.util.response :as ring-resp]
            [io.pedestal.http.route :refer [url-for]]
            [playground.services.session.login.logic :as logic]))

(spec/def ::username (spec/and string? seq (complement clojure.string/blank?)))
(spec/def ::password (spec/and string? seq (complement clojure.string/blank?)))
(spec/def ::api (spec/keys :req-un [::username ::password]))

(defn password-by-username [{:keys [db] :as request} username]
  (let [db (->> db :pool (hash-map :datasource))]
    (->> (logic/query-pass-by-user username)
         (h/format)
         (jdbc/query db)
         (first))
    ))

(defn perform [request]
  (let [username (get-in request [:form-params :username])
        password (get-in request [:form-params :password])
        session (:session request)]
    (if (logic/check-password password (:password (password-by-username request username)))
      (let [next-url (get-in request [:query-params :next] "/")
            updated-session (assoc session :identity {:username username :password password})]
                 (-> (ring-resp/redirect next-url)
                     (assoc :session updated-session)))
      (-> (ring-resp/redirect (url-for :login))
          (assoc :flash "wrong password")))))


