(ns playground.services.session.register.logic
  (:require
   [buddy.hashers :as hashers]
   [honeysql.helpers :as hh]))

(defn to-insert [username password]
  (-> (hh/insert-into :register)
      (hh/values [{:username username
                   :password password}])))

(defn to-check [username]
  (-> (hh/select :username)
      (hh/from :register)
      (hh/where [:= :username username])))

(defn derive-password [password]
  (hashers/derive password))
