(ns playground.services.invoices.retrieve.logic)

(defn to-query [id]
  {:select [:*]
   :from   [:users]
   :where  [:= :id id]})

(defn get-author [id]
  {:select [:author]
   :from [:users]
   :where [:= :id id]})
