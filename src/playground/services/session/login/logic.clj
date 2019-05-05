(ns playground.services.session.login.logic
  (:require
   [buddy.hashers :as hashers]
   [honeysql.helpers :refer :all]))

(defn to-query [username password]
  (-> (select :*)
      (from   :register)
      (where  [:= :username username] [:= :password password])))

(defn query-pass-by-user [username]
  (-> (select :encrypted_password)
      (from :register)
      (where [:= :username username])))

(defn check-password [password encrypted]
  (hashers/check password encrypted))

(defn role [username]
  (-> (select :role)
      (from :register)
      (where [:= :username username])))
