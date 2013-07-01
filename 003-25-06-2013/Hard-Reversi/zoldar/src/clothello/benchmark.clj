(ns clothello.benchmark
  (:require [clothello.logic :as logic]
            [clothello.player :as player]
            [table.core :refer [table]]))

(defn benchmark-players [player1 player2]
  (frequencies (repeatedly 10 #(-> (logic/create-game logic/classic-board 
                                                       player1 player2) 
                                    reverse 
                                    first 
                                    :board 
                                    logic/get-winner))))

(defn render-ratio [ratios name1 name2]
  (if-let [[wins1 wins2] (get ratios [name1 name2])]
    (str (or wins1 0) ":" (or wins2 0))
    "X"))

(defn render-table [names ratios]
  (let [first-row (concat [""] names)
        rows (for [name1 names]
               (concat [name1] (map (partial render-ratio ratios name1) 
                                    names)))]
    (table (concat [first-row] rows) :style :org)))

(defn benchmark []
  (let [players (-> (player/get-players) (dissoc "human"))
        names (keys players)
        ratios (into {} (for [[name1 player1] players 
                              [name2 player2] players]
                          (let [result (benchmark-players player1 player2)
                                ratio ((juxt :dark :light) result)]
                            [[name1 name2] ratio])))]
    (println (render-table names ratios))))
