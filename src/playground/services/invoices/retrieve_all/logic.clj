(ns playground.services.invoices.retrieve-all.logic)

(defn query-all []
  {:select [:*]
   :from [:users]})
