(ns playground.services.invoices.update.view
  (:require [hiccup.page :as page]
            [hiccup.table :as table]
            [playground.views :as views]))

(defn submit-invoice [context]
  (page/html5
   (views/gen-page-head "All Entries")
   [:div
    [:h2 (:confirmation context)]
    [:h1 "All Entries"]
    [:div
     (let [attr-fns {:data-value-transform
                       (fn [label-key v]
                         (if (= :id label-key)
                           [:a {:href (str "/invoices/" v)} v]
                           (if (= :delete label-key)
                             [:a {:href (str "/invoices-delete/" v)} "delete"]
                             v)))}
           extended-context (map #(assoc % :delete (:id %)) (:result context))]
         (table/to-table1d
          extended-context
          [:id "ID" :email "Email" :delete "to-delete"]
          attr-fns))]]))

