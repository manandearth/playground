(ns playground.services.invoices.retrieve.view
  (:require [hiccup.page :as page]
            [playground.views :as views]))


(defn update-invoice [entry]
  (page/html5
   (views/gen-page-head "Invoice")
   (views/header-links entry)
   [:div
    [:h1 (str "Update entry id: " (:id entry))]
    [:form {:action (str "/invoices-update/" (:id entry)) :method "POST"}
      [:div
       [:p [:label "id: " [:input {:type "hidden" :name "id" :value (:id entry)}]] [:span (:id entry)]]
       [:p [:label "amount: " [:input {:type "text" :name "name" :value (first (clojure.string/split (:email entry) #"@"))}]]]
       [:p [:label "λ ->" [:input {:type "submit" :value "Update"}]]]]]]))
