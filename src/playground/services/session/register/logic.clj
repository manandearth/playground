(ns playground.services.session.register.logic
  (:require
   [honeysql.helpers :as hh]))

(defn to-insert [username password]
  (-> (hh/insert-into :register)
      (hh/values [{:username username
                   :password password}])))

