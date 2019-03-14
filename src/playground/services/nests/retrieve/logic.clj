(ns playground.services.nests.retrieve.logic)

(defn to-query [species]
  {:select [:*]
   :from [:nests]
   :where [:= :species species]})
