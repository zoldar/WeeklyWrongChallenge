(ns easy-4clojure-131.submissions.locks
  (:require [clojure.math.combinatorics  :as combo]
            clojure.set))


(defn non-empty-subsets [sets]
  (->> sets
       combo/subsets
       (remove empty?)
       (map (partial reduce + ))
       set))

(defn solution [& sets]
  (let [summed-subsets (map non-empty-subsets sets)]
    (not (empty? (apply clojure.set/intersection
                        summed-subsets)))))