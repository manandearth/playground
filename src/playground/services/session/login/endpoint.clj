(ns playground.services.session.login.endpoint
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core :as h]
            [clojure.spec.alpha :as spec]
            [playground.services.session.login.logic :as logic]))


(spec/def ::username (spec/and string? (spec/nilable not-empty)))
(spec/def ::password (spec/and string? (spec/nilable not-empty)))
(spec/def ::api (spec/keys :req-un [::username ::password]))


(defn login-authenticate
  "Check request username and password against authdata
  username and passwords.
  On successful authentication, set appropriate user
  into the session and redirect to the value of
  (:next (:query-params request)). On failed
  authentication, renders the login page."
  [{{:keys [username password]} :form-params :keys [db session] :as request}]
  (let [db  (->> db :pool (hash-map :datasource))
        match (->> (logic/to-query username password)
                   (h/format)
                   (jdbc/query db)
                   (first))]
    (if match
      (let [updated-session (assoc session :identity (keyword username))]
        (-> {:status 301 :headers {"Location" "/greet"} :body "" :flash "Login succesful"}
            (assoc :session updated-session)
            (assoc :flash "Login succesful")))
      
      {:status 301 :headers {"Location" "/login"} :body "" :flash "Wrong username/password"})))
