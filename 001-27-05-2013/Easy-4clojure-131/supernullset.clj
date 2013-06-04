;; My thinking was simply thus:
;; 1: write function that finds subsets of a set
;; 2: write function that sums subsets of aforementioned set
;; 3: check for intersection

(defn subsets
  "takes a coll, returns a coll of subsets of that coll"
  [items]
  (cond
   (= (count items) 0) '(())
   (empty? items) '()
   :else (concat (map
                  #(cons (first items) %)
                  (subsets (rest items))
                  )
                 (subsets (rest items)))
   )
)

(defn sum
  "convenience function to fit my semantics"
  [x] (reduce + x))

(defn sum-subsets
  "takes a coll of coll, returns set of all possible sumations in a subset.
   The triple map is gross and needs to be remedied.n"
  [x]
  (cond
   (= (count x) 1) x
   (empty? x) '()
   :else (map set (map #(map sum (filter not-empty (subsets %))) x))
   )
  )

(defn equiv-sum
  "convenience function to check for interection"
  [& x]
  (let [temp (apply clojure.set/intersection (sum-subsets x))]
    (if (not-empty temp)
      true
      false
      )
    )
  )

;; test cases
(= true  (equiv-sum #{-1 1 99}
                    #{-2 2 888}
                    #{-3 3 7777}))
;; => true

(= false (equiv-sum #{1}
             #{2}
             #{3}
             #{4}))
;; => true

(= true  (equiv-sum #{1}))
;; => true

(= false (equiv-sum #{1 -3 51 9}
             #{0}
             #{9 2 81 33}))
;; => true

(= false (equiv-sum #{1 -3 51 9}
             #{0}
             #{9 2 81 33}))
;; => true

(= true  (equiv-sum #{1 3 5}
             #{9 11 4}
             #{-3 12 3}
             #{-3 4 -2 10}))
;; => true

(= false (equiv-sum #{-1 -2 -3 -4 -5 -6}
             #{1 2 3 4 5 6 7 8 9}))
;; => true

(= true  (equiv-sum #{1 3 5 7}
             #{2 4 6 8}))
;; => true

(= true  (equiv-sum #{-1 3 -5 7 -9 11 -13 15}
             #{1 -3 5 -7 9 -11 13 -15}
             #{1 -1 2 -2 4 -4 8 -8}))
;; => true

(= true  (equiv-sum #{-10 9 -8 7 -6 5 -4 3 -2 1}
                    #{10 -9 8 -7 6 -5 4 -3 2 -1}))
;; => true
