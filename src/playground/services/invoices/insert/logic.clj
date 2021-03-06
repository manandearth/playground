(ns playground.services.invoices.insert.logic
  (:require
   [honeysql.helpers :as hh]))

(defn to-insert [amount random-uuid username]
  (-> (hh/insert-into :users)
      (hh/values [{:email (str amount "@" random-uuid ".com")
                   :author username}])))

(def to-query
  {:select   [:id :email :author]
   :from     [:users]
   :order-by [[:id :desc]]
   :limit    1})

(defn to-serialize [results]
  (-> results first (select-keys [:id :email :author])))
