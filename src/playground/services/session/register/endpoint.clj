(ns playground.services.session.register.endpoint
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.spec.alpha :as spec]
            [honeysql.core :as h]
            [playground.services.session.register.logic :as logic]))

(spec/def ::username (spec/and string? (spec/nilable not-empty)))
(spec/def ::password (spec/and string? (spec/nilable not-empty)))
(spec/def ::api (spec/keys :req-un [::username ::password]))



(defn perform  [{{:keys [username password]} :form-params :keys [db] :as request}]
  (let [db     (->> db :pool (hash-map :datasource))
        query (-> (logic/to-check username)
                  (h/format))
        check (jdbc/query db query)
        insert (-> (logic/to-insert username password)
                   (h/format))]
    (if (empty? check)
      (do (jdbc/execute! db insert)
          {:status 301 :headers {"Location" "/login" } :body "" :flash username})
      {:status 301 :headers {"Location" "/register"} :body "" :flash "username already taken, choose another"})))
