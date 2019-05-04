(ns seeds.sql
  (:require
   [clojure.java.jdbc :as j]
   [honeysql.core :as h]
   [playground.services.session.register.logic :as logic]))

(defn run [target]
  (let [db {:connection-uri (-> target :db :url)}
        username "admin"
        password "admin"
        role "admin"
        insert (-> (logic/to-insert username (logic/derive-password password) role)
                   (h/format))]
    (do (j/execute! db insert)
        (j/insert-multi! db :users [{:email  "663@7e761522-cc50-47f5-9df0-231ac73d98d1.com" :author "admin"}
                                    {:email  "664@7e762523-dd50-47f5-9de1-231bc73e97d2.com" :author "admin"}
                                    {:email  "665@7e763524-ee50-47f5-9dd2-231cc73f96d3.com" :author "admin"}
                                    {:email  "666@7e764525-ff50-47f5-9dc3-231dc73a95d4.com" :author "admin"}]))))
