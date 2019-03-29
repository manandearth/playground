(ns playground.services.session.login.logic
  (:require [honeysql.core :as h]
            [honeysql.helpers :refer :all :exclude [update]]))

(defn to-query [username password]
  (-> (select :*)
      (from   :register)
      (where  [:= :username username] [:= :password password])))


(defn query-all-usernames []
  (-> (select :username)
      (from   :register)))

;(h/format (to-query "beam" "boom"))

(defn query-pass-by-user [username]
  (-> (select :password)
      (from :register)
      (where [:= :username username])))
