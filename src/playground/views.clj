(ns playground.views
  (:require
   [hiccup.page :as page]
   [hiccup.table :as table]
   [playground.services.invoices.retrieve-all.endpoint :as retrieve-all]
   [playground.services.invoices.retrieve.endpoint :as retrieve]))

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

(defn all-invoices [context]
  (page/html5
   (gen-page-head "All Entries")
   header-links
   [:div
    [:h1 "All Entries"]
    [:div
     (let [attr-fns {:data-value-transform
                     (fn [label-key v]
                       (if (= :id label-key)
                         [:a {:href (str "/invoices/" v)} v]
                         v))}]
       (table/to-table1d
        (retrieve-all/perform context)
        [:id "ID" :email "Email"]
        attr-fns))]]))

(defn invoice [context]
  (page/html5
   (gen-page-head "Invoice")
   header-links
   [:div
    [:h1 "Invoice"]
    [:p (str (retrieve/perform context))]]))
