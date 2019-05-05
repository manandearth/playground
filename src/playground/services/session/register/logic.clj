(ns playground.services.session.register.logic
  (:require
   [buddy.hashers :as hashers]
   [honeysql.helpers :as hh]))

(defn to-insert [username encrypted-password & [role]]
  (let [values {:username username :encrypted_password encrypted-password}]
    (-> (hh/insert-into :register)
        (hh/values [(if-not role
                      values
                      (assoc values :role role))]))))

(defn to-check [username]
  (-> (hh/select :username)
      (hh/from :register)
      (hh/where [:= :username username])))

(defn derive-password [password]
  (hashers/derive password))
