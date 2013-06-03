(ns hangman.core
  (:gen-class))

;; Game logic

;; Example of data structure representing game state
(def ^:private game {:mistakes 0 :word [\h \a \n \d] :guess [:_ :_ :_ :_] :inputs #{}})

(defn game-lost? [{:keys [mistakes]}]
  (>= mistakes 9))

(defn game-won? [{:keys [guess] :as game}]
  (and (not (game-lost? game)) (not-any? #(= % :_) guess)))

(defn game-finished? [game]
  (or (game-lost? game) (game-won? game)))

(defn fill-gaps [word guess input]
  (map (fn [w-char g-char] (if (= w-char input) input g-char)) word guess))

(defn make-guess [{:keys [mistakes word guess inputs] :as game} input]
  (let [new-guess (fill-gaps word guess input)
        new-inputs (conj inputs input)]
    (if (and (= new-guess guess) (not (inputs input)))
      (assoc game :mistakes (inc mistakes) :inputs new-inputs)
      (assoc game :guess new-guess :inputs new-inputs))))

(defn game-step [inputs game]
  (let [[input inputs] ((juxt first rest) inputs)]
      (when-not (or (game-finished? game) (not input))
        (let [new-game (make-guess game input)]
          (cons new-game (lazy-seq (game-step inputs new-game)))))))

(defn create-game [word inputs]
  (let [word-seq (seq word)
        initial-guess (repeat (count word-seq) :_)
        initial-game {:mistakes 0 :word word-seq :guess initial-guess :inputs #{}}]
    (cons initial-game (lazy-seq (game-step inputs initial-game)))))

;; UI code

;; Stolen from Sean's submission
(def ^:private full-gallows
  [""
   "\n|\n|\n|\n|"
   "____\n|\n|\n|\n|"
   "____\n|  |\n|\n|\n|"
   "____\n|  |\n|  o\n|\n|"
   "____\n|  |\n|  o\n|  |\n|"
   "____\n|  |\n|  o\n| /|\n|"
   "____\n|  |\n|  o\n| /|\\\n|"
   "____\n|  |\n|  o\n| /|\\\n| /"
   "____\n|  |\n|  o\n| /|\\\n| / \\"])

(defn take-input []
  (println "Make a guess or type quit: ")
  (let [guess (read-line)]
    (when (= guess "quit")
      (System/exit 0))
    (if (not= (count guess) 1)
      (do
        (println "Give single character or quit. Try again.")
        (recur))
      (first guess))))

(defn lazy-input [input-fn]
  (let [step (fn step []
               (cons (input-fn) (lazy-seq (step))))]
    (lazy-seq (step))))

(defn render-guess [guess]
  (let [new-guess (map #(if (keyword? %) (name %) %) guess)]
    (apply str (interleave new-guess (repeat " ")))))

(defn display-state [{:keys [mistakes guess inputs] :as game-stage}]
  (println (nth full-gallows mistakes))
  (println (str "Current guess: " (render-guess guess)))
  (println (str "Letters used so far: " (apply str (-> inputs sort (interleave (repeat " ")))))))

(defn end-game-success [game-stage]
  (display-state game-stage)
  (println "You made it!")
  (System/exit 0))

(defn end-game-fail [game-stage]
  (display-state game-stage)
  (println "You lost!")
  (System/exit 0))

(defn play-game [word]
  (let [game (create-game word (lazy-input take-input))]
    (doseq [stage game]
      (cond (game-won? stage) (end-game-success stage)
            (game-lost? stage) (end-game-fail stage)
            :else (display-state stage)))))

(defn -main [& [word & _]]
  (play-game word))
