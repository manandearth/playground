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
         context
        [:id "ID" :email "Email"]
        attr-fns))]]))

(defn update-invoice [user]
  (page/html5
   (gen-page-head "Invoice")
   header-links
   [:div
    [:h1 (str "Update entry id: " (:id user))]
    [:form {:action (str "/invoices-update/" (:id user)) :method "POST"}
      [:div
       [:p [:label "id: " [:input {:type "hidden" :name "id" :value (:id user)}]] [:span (:id user)]]
       [:p [:label "amount: " [:input {:type "text" :name "name" :value (first (clojure.string/split (:email user) #"@"))}]]]
       [:p [:label "λ ->" [:input {:type "submit" :value "Update"}]]]]]]))

(defn submit-invoice [context]
  (page/html5
   (gen-page-head "All Entries")
   header-links
   [:div
    [:h2 (:confirmation context)]
    [:h1 "All Entries"]
    [:div
     (let [attr-fns {:data-value-transform
                     (fn [label-key v]
                       (if (= :id label-key)
                         [:a {:href (str "/invoices/" v)} v]
                         v))}]
       (table/to-table1d
        (:result context)
        [:id "ID" :email "Email"]
        attr-fns))]]))

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
