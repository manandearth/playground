(ns playground.services.invoices.retrieve-all.view
  (:require
   [hiccup.page :as page]
   [hiccup.table :as table]
   [io.pedestal.http.route :refer [url-for]]
   [playground.views :as views]))

(defn all-invoices [request context & flash]
  (page/html5
   (views/gen-page-head "All Entries")
   (views/header-links request)
   [:div
    (when (seq flash) [:h2 flash])
    [:h1 "All Entries"]
    [:div
     (let [attr-fns {:data-value-transform
                     (fn [label-key v]
                       (if (= :id label-key)
                         [:a {:href (url-for :invoices/:id :path-params {:id v})} v]
                         (if (= :delete label-key)
                           [:a {:id (str "delete-" v) :href (url-for :invoices-delete/:id :path-params {:id v})}"delete"]
                           v)))}
           extended-context (map #(assoc % :delete (:id %)) context)]
       (table/to-table1d
        extended-context
        [:id "ID" :email "Email" :author "Author" :delete "to-delete"]
        attr-fns))]]))


