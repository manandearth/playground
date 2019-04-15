(ns playground.services.invoices.retrieve.logic)

(defn to-query [id]
  {:select [:*]
   :from   [:users]
   :where  [:= :id id]})

(defn get-authored [id]
  {:select [:authored]
   :from [:users]
   :where [:= :id id]})
