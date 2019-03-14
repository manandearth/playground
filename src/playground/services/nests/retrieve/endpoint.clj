(ns playground.services.nests.retrieve.endpoint
  (:require
   [cheshire.generate]
   [clojure.java.jdbc :as jdbc]
   [clojure.spec.alpha :as spec]
   [honeysql.core :as h]
   [playground.services.nests.retrieve.logic :as logic])
  (:import
   [org.postgresql.jdbc4 Jdbc4Array]))

(cheshire.generate/add-encoder Jdbc4Array (fn [c json-generator]
                                            (-> c .getArray (cheshire.generate/encode-seq json-generator))))

(spec/def ::species nat-int?)

(spec/def ::api (spec/keys :req-un [::species]))

(defn perform [{{:keys [species]} :path-params :keys [db] :as request}]
  (let [db     (->> db :pool (hash-map :datasource))
        record (->> (logic/to-query species)
                    (h/format)
                    (jdbc/query db)
                    (first))]
    (if record
      {:status 200
       :body   record}
      {:status 404})))
