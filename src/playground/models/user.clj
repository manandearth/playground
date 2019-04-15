(ns playground.models.user
  (:require [clojure.spec.alpha :as spec]
            [clojure.string :as string]))

(spec/def ::username (spec/and string? seq (complement clojure.string/blank?)))
(spec/def ::password ::username)
(spec/def ::name ::username)

(spec/def ::id nat-int?)

(spec/def ::amount nat-int?)

(def admin-role "admin")
