(ns playground.services.session.register.endpoint
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.spec.alpha :as spec]
            [honeysql.core :as h]
            [playground.services.session.register.logic :as logic]))

(defn perform  [{{:keys [username password]} :form-params :keys [db] :as request}]
  (let [db     (->> db :pool (hash-map :datasource))
        insert (-> (logic/to-insert username password)
                   (h/format))
        _      (jdbc/execute! db insert)
        ] 
    {:status 301 :headers {"Location" "/login" } :body "" :flash "Registered"}))
