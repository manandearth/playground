(ns playground.services.session.login.logic
  (:require [honeysql.core :as h]
            [honeysql.helpers :refer :all :exclude [update]]))

(defn to-query [username password]
  (-> (select :*)
      (from   :register)
      (where  [:= :username username] [:= :password password])))

(defn query-pass-by-user [username]
  (-> (select :password)
      (from :register)
      (where [:= :username username])))
