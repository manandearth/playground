(ns playground.services.invoices.update.logic
  (:require
   [honeysql.core :as h]
   [honeysql.helpers :as hh]
   [honeysql.types :as ht]
   ))

(defn to-update [id amount uuid]
  (-> (hh/update :users)
      (hh/sset  {:email (str amount "@" uuid ".com")})
      (hh/where [:= :id (Integer/parseInt id)])))

(def to-query
  {:select   [:id :email]
   :from     [:users]
   :order-by [[:id :desc]]
   :limit    1})

(defn to-serialize [results]
  (-> results first (select-keys [:id :email])))
