(ns playground.models.user
  (:require [clojure.spec.alpha :as spec]
            [clojure.string :as string]))

(spec/def ::present-string (spec/and string? seq (complement clojure.string/blank?)))
(spec/def ::username ::present-string)
(spec/def ::password ::present-string)
(spec/def ::name ::present-string)

(spec/def ::id nat-int?)

(spec/def ::amount nat-int?)

(def admin-role "admin")
