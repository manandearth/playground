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
   " | "
   [:a {:href "/login"} "Login"]
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

(defn login []
  (page/html5
   (gen-page-head "login")
   header-links
   [:div
    [:h1 "Login"]
    [:form {:action "/login" :method "POST"}
     [:div
      [:p [:label "User name: " [:input {:type "text" :name "username"}]]]
      [:p [:label "Password: " [:input {:type "text" :name "password"}]]]
      [:p [:label "" [:input {:type "submit" :value "submit"}]]]
      ]]
    [:div  [:a {:href "/register"} "Register here"]]]))



(defn register []
  (page/html5
   (gen-page-head "Register")
   header-links
   [:div
    [:h1 "Register"]
    [:form {:action "/register" :method "POST"}
     [:div
      [:p [:label "User name: " [:input {:type "text" :name "username"}]]]
      [:p [:lable "Password: " [:input {:type "text" :name "password"}]]]
      [:p [:label "" [:input {:type "submit" :value "submit"}]]]]]]))
