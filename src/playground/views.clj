(ns playground.views
  (:require
   [hiccup.page :as page]
   [hiccup.table :as table]))

(defn gen-page-head
  [title]
  [:head [:title title]]
  )

(def header-links
  [:div
   "[ "
   [:a {:href "/"} "Home"]
   " | "
   [:a {:href "/about"} "About"]
   " | "
   [:a {:href "/invoices-insert"} "Add an entry"]
   " | "
   [:a {:href "/invoices"} "All Entries"]
   " ]"])

(defn home []
  (page/html5
   (gen-page-head "Home")
   header-links
   [:div
    [:h1 "Hello World!"]]))

(defn about []
  (page/html5
   (gen-page-head "About")
   header-links
   [:div
    [:h1 "About"]
    [:p (format "Clojure %s"
               (clojure-version))]]))

(defn insert []
  (page/html5
   (gen-page-head "Add an entry")
   header-links
    [:div
     [:h1 "Add an entry to the DB"]
     [:form {:action "/invoices-insert" :method "POST"}
      [:div
       [:p [:label "amount: " [:input {:type "text" :name "amount"}]]]
       [:p [:label "λ ->" [:input {:type "submit" :value "Submit"}]]]]]]))
