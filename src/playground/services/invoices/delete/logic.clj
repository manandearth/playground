(ns playground.services.invoices.delete.logic
  (:require
   [honeysql.helpers :as hh]))

(defn to-delete [id]
  {:delete-from :users
   :where [:= :id id]})


(defn to-serialize []
  {:select [:%count.*]
   :from   [:users]})
