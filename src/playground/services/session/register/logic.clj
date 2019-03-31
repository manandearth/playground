(ns playground.services.session.register.logic
  (:require
   [honeysql.helpers :as hh]
   [honeysql.core :as h]))

(defn to-insert [username password]
  (-> (hh/insert-into :register)
      (hh/values [{:username username
                   :password password}])))

(defn to-check [username]
  (-> (hh/select :username)
      (hh/from :register)
      (hh/where [:= :username username])))
