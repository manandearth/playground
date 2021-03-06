(ns playground.services.invoices.update.logic
  (:require
   [honeysql.helpers :as hh]))

(defn to-update [id name uuid]
  (-> (hh/update :users)
      (hh/sset  {:email (str name "@" uuid ".com")})
      (hh/where [:= :id id])))

(def to-query
  {:select   [:id :email :author]
   :from     [:users]
   :order-by [[:id :desc]]
   :limit    1})

(defn to-serialize [results]
  (-> results first (select-keys [:id :email :author])))
