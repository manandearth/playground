(ns playground.services.invoices.retrieve-all.view
  (:require
   [hiccup.page :as page]
   [hiccup.table :as table]
   [playground.views :as views]))

(defn all-invoices [context]
  (page/html5
   (views/gen-page-head "All Entries")
   views/header-links
   [:div
    [:h1 "All Entries"]
    [:div
     (let [attr-fns {:data-value-transform
                     (fn [label-key v]
                       (if (= :id label-key)
                         [:a {:href (str "/invoices/" v)} v]
                         v))}]
       (table/to-table1d
         context
        [:id "ID" :email "Email"]
        attr-fns))]]))
