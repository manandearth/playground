(ns playground.services.session.register.logic
  (:require
   [honeysql.helpers :as hh]
   [honeysql.core :as h]
   [buddy.hashers :as hashers]))

(defn to-insert [username password & role]
  (let [values {:username username :password password}]
    (-> (hh/insert-into :register)
        (hh/values [(if-not role
                       values
                       (assoc values :role (first role))
                       )]))))

(defn to-check [username]
  (-> (hh/select :username)
      (hh/from :register)
      (hh/where [:= :username username])))

(defn derive-password [password]
  (hashers/derive password))

