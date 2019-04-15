(ns playground.services.invoices.insert.logic
  (:require
   [honeysql.helpers :as hh]))

(defn to-insert [amount random-uuid session]
  (-> (hh/insert-into :users)
      (hh/values [{:email (str amount "@" random-uuid ".com")
                   :authored (get-in session [:identity :username])}])))

(def to-query
  {:select   [:id :email :authored]
   :from     [:users]
   :order-by [[:id :desc]]
   :limit    1})

(defn to-serialize [results]
  (-> results first (select-keys [:id :email :authored])))
