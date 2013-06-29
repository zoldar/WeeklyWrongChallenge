(ns clothello.ai.simple
  (:require [clothello.player :as player]
            [clothello.logic :as logic]))

(defn make-random-decision [side history board]
  (when-let [valid-moves (seq (logic/get-valid-moves side board))] 
    (rand-nth valid-moves)))

(defn create-random-ai-player [side]
  (player/create-ai-player side make-random-decision))

(defn make-greedy-decision [side history board]
  (when-let [valid-moves (seq (logic/get-valid-moves side board))] 
    (apply max-key #(count (logic/get-pieces-to-flip side board %)) valid-moves)))

(defn create-greedy-ai-player [side]
  (player/create-ai-player side make-greedy-decision))

(player/register-player "random" create-random-ai-player)
(player/register-player "greedy" create-greedy-ai-player)
