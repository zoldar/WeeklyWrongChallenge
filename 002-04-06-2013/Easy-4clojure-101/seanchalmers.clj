(def str1 "FooFoo")
(def str2 "BarBar")

(def str4 "FooBar")

(def str3 "FooFooFoo")

(defn zero-str? [a]
  (= (count a) 0))

(defn leve-dist [a b]
  (apply + (map #(if (= %1 %2) 0 1) a b)))

;; Calculate the Levenshtein Distance between two strings.
(defn shtein-dist [a b]
  ;; For Convience keep a count of the length of the strings.
  (let [countA (count a)
        countB (count b)]
    ;; Handle the input of empty strings.
    (if (or (zero-str? a) (zero-str? b))
      ;; Doesn't matter which is zero because the length
      ;; of the other string will be the distance between.
      (max countA countB)
      (if (> countA countB)
        ;; My solution only works if the map runs over the
        ;; longer of the two input strings.
        (leve-dist a b)
        (leve-dist b a)))))

;;;;;;;;;;
;; Some random test cases.
;;;;;;;;;;

(zero-str? "")
(zero-str? str3)

;; Doesn't cover every case :\
(shtein-dist str3 str4)

(shtein-dist str1 str2)

(shtein-dist "" str4)