
(def list-sets '(#{-1 1 99} #{-2 2 888} #{-3 3 7777}))

(defn subsets [items]
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

(defn sum [x] (reduce + x))

(defn sum-subsets [x]
  (cond
   (= (count x) 1) x
   (= (count x) 0) '(())
   (empty? x) '()
   :else (map set (map #(map sum (filter not-empty (subsets %))) x))
   )
  )

(defn equiv-sum [& x]
  (let [temp (apply clojure.set/intersection (sum-subsets x))]
    (if (not-empty temp)
      true
      false
      )
    )
  )
