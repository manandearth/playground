(ns playground.services.invoices.retrieve.view
  (:require [hiccup.page :as page]
            [playground.views :as views]))


(defn update-invoice [user]
  (page/html5
   (views/gen-page-head "Invoice")
   [:div
    [:h1 (str "Update entry id: " (:id user))]
    [:form {:action (str "/invoices-update/" (:id user)) :method "POST"}
      [:div
       [:p [:label "id: " [:input {:type "hidden" :name "id" :value (:id user)}]] [:span (:id user)]]
       [:p [:label "amount: " [:input {:type "text" :name "name" :value (first (clojure.string/split (:email user) #"@"))}]]]
       [:p [:label "λ ->" [:input {:type "submit" :value "Update"}]]]]]]))
