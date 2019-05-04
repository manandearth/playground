(ns playground.services.invoices.delete.logic)

(defn to-delete [id]
  {:delete-from :users
   :where [:= :id id]})

(defn to-serialize []
  {:select [:%count.*]
   :from   [:users]})
