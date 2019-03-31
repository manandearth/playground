(ns playground.services.session.login.endpoint
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core :as h]
            [clojure.spec.alpha :as spec]
            [playground.services.session.login.logic :as logic]))

(spec/def ::username (spec/and string? (spec/nilable not-empty)))
(spec/def ::password (spec/and string? (spec/nilable not-empty)))
(spec/def ::api (spec/keys :req-un [::username ::password]))


(defn all-usernames [{:keys [db] :as request}]
  (let [db (->> db :pool (hash-map :datasource))]
    (->> (logic/query-all-usernames)
         (h/format)
         (jdbc/query db))))

(defn password-by-username [{:keys [db] :as request} username]
  (let [db (->> db :pool (hash-map :datasource))]
    (->> (logic/query-pass-by-user username)
         (h/format)
         (jdbc/query db))))


(defn perform [{{:keys [username password]} :form-params :as request}]
  {:status 301 :headers {"Location" "/"} :body ""})
