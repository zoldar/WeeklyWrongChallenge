(ns clothello.core
  (:require [clothello.logic :as logic])
  (:gen-class))

(declare lazy-input take-input)

(def players {"human" #(logic/create-human-player % (lazy-input take-input %))
              "random" logic/create-random-ai-player
              "greedy" logic/create-greedy-ai-player})

(def render-symbols {:dark "D" :light "L" :empty "X"})

(defn quit []
  (System/exit 0))

(defn parse-input [input]
  (let [numbers (map #(Integer/parseInt %) (re-seq #"\d+" input))]
    (when (and (= (count numbers) 2) 
               (logic/within-boundaries? numbers))
      numbers)))

(defn take-input [side]
  (println (apply str "It's " (name side) "'s turn."))
  (println "Type a position (two numbers delimited with space) or type quit: ")
  (let [position (read-line)]
    (when (= position "quit")
      (quit))
    (if-let [parsed-position (parse-input position)]
      parsed-position
      (do
        (println "Invalid input. Try again.")
        (recur side)))))

(defn lazy-input [input-fn side]
  (let [step (fn step []
               (cons (input-fn side) (lazy-seq (step))))]
    (lazy-seq (step))))

(defn render-board [board]
  (dorun (apply map (fn [& columns] 
                      (println (apply str (map render-symbols columns)))) 
                board))
  (println ""))

(defn end-game [{:keys [board]}]
  (println "Game finished!")
  (println (apply str (name (logic/get-winner board)) " wins!"))
  (quit))

(defn play-game [player1 player2]
  (doseq [{:keys [board] :as game-stage} 
          (logic/create-game logic/classic-board player1 player2)]
    (render-board board)
    (when (logic/game-finished? board) 
      (end-game game-stage))))

(defn -main [& [player1 player2 & _]]
  (play-game (get players player1) (get players player2)))
