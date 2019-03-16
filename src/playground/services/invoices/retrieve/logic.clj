(ns playground.services.invoices.retrieve.logic)

(defn to-query [id]
  {:select [:*]
   :from   [:users]
   :where  [:= :id id]})

(defn all-query []
  {:select [:*]
   :from [:users]})
