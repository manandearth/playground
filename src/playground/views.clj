(ns playground.views
  (:require
   [hiccup.page :as page]
   [hiccup.table :as table]))

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

(defn gen-page-head
  [title]
  [:head [:title title]]
  header-links)

(defn home []
  (page/html5
   (gen-page-head "Home")
   [:div
    [:h1 "Hello World!"]]))

(defn about []
  (page/html5
   (gen-page-head "About")
   [:div
    [:h1 "About"]
    [:p (format "Clojure %s"
               (clojure-version))]]))

(defn insert []
  (page/html5
   (gen-page-head "Add an entry")
   [:div
     [:h1 "Add an entry to the DB"]
     [:form {:action "/invoices-insert" :method "POST"}
      [:div
       [:p [:label "amount: " [:input {:type "text" :name "amount"}]]]
       [:p [:label "λ ->" [:input {:type "submit" :value "Submit"}]]]]]]))

(defn login []
  (page/html5
   (gen-page-head "login")
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
   [:div
    [:h1 "Register"]
    [:form {:action "/register" :method "POST"}
     [:div
      [:p [:label "User name: " [:input {:type "text" :name "username"}]]]
      [:p [:lable "Password: " [:input {:type "text" :name "password"}]]]
      [:p [:label "" [:input {:type "submit" :value "submit"}]]]]]]))

(defn greet [identity]
  (page/html5
   (gen-page-head "greet")
   [:div
    [:h2 (str  "welcome back, " identity ".")]]))
