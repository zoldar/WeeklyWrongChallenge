(def str1 "FooFoo")
(def str2 "BarBar")

(def str4 "FooBar")

(def str3 "FooFooFoo")

(defn compare-pants [a b]
  (if (= a b) 0 1))

(defn shtein-dist [a b]
  (apply + (map #(compare-pants %1 %2) a b)))

;; Doesn't cover every case :\
(shtein-dist str3 str4)

(shtein-dist str1 str2)
