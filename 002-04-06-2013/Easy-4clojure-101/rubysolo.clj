(defn levenshtein [s1 s2]
  (cond
    (empty? s1) (count s2)
    (empty? s2) (count s1)
    :else
      (let [cost (if (= (last s1) (last s2)) 0 1)]
        (min (inc (levenshtein (butlast s1) s2))
             (inc (levenshtein s1 (butlast s2)))
             (+ cost (levenshtein (butlast s1) (butlast s2)))))
  ))

;; (assert (= 0 (levenshtein [] [])))
;; (assert (= 1 (levenshtein [1] [])))
;; (assert (= 1 (levenshtein [1 2 3] [1 2])))
;; (assert (= 1 (levenshtein [1 2 3] [1 2 4])))
;; (assert (= 2 (levenshtein [7 2 8] [1 2 3])))
;; (assert (= 2 (levenshtein [1 2 3] [2 3 4])))
