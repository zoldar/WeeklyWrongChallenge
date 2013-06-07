(def str1 "FooFoo")
(def str2 "BarBar")

(def str4 "FooBar")

(def str3 "FooFooFoo")

(defn compare-pants [a b]
  (if (= a b) 0 1))

(defn zero-str? [a]
  (= (count a) 0))

(defn leve-dist [a b]
  (apply + (map #(compare-pants %1 %2) a b)))

(defn shtein-dist [a b]
  (if (or (zero-str? a) (zero-str? b))
    (max (count a) (count b))
    (if (> (count a) (count b))
      (leve-dist a b))
      (leve-dist b a)))

(zero-str? "")
(zero-str? str3)

;; Doesn't cover every case :\
(shtein-dist str3 str4)

(shtein-dist str1 str2)

(shtein-dist "" str4)