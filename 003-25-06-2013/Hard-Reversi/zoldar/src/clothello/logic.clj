(ns clothello.logic)

;; Reversi rules in short bullet points:
;; - the game takes place on a square board of dimensions 8x8 squares
;; - two players take part in the game, represented by light and dark pieces
;; - the standard starting position is in the group of central four squares 
;;   which are filled with 4 pieces - 2 pieces per player in a following layout
;;   (D - dark, L - light)
;; L | D
;; -----
;; D | L
;; - player with dark pieces moves first
;; - players in turns place a piece with their color on the board 
;;   in such a position that there exists at least one straight (horizontal, 
;;   vertical, or diagonal) occupied line between the new piece and another 
;;   piece of their color, with one or more contiguous pieces of opposite color 
;;   between them
;; - after placing the piece, all pieces of opposite color lying on a straight line 
;;   between the new piece and any anchoring piece of the given player's color are
;;   changed to the same color
;; - a valid move is one where at least one piece is reversed
;; - if one player cannot make a valid move, play passes back to the other player
;; - when neither player can make a valid move, the game ends
;; - the player with the most pieces on the board at the end of the game wins

(defn valid-move? [board side x y])

(defn game-finished? [game])

(defn create-human-player [side input-fn]
  (partial human-player side input-fn))

(defn human-player [side input-fn board]
  {:move (input-fn) :move-fn (partial human-player side input-fn)})

(defn create-ai-player [side decision-fn]
  (partial ai-player side [] decision-fn))

(defn ai-player [side history decision-fn board]
  (let [move-fn (partial ai-player side (conj history board) decision-fn)]
    {:move (decision-fn side history board) :move-fn move-fn}))

(defn lazy-input [])

(defn create-human-cli-player [side]
  (create-human-player side lazy-input))

(defn make-random-decision [side history board]
  [2 3])

(defn create-random-ai-player [side]
  (create-ai-player side make-random-decision))

(defn create-initial-game [initial-board dark-player-constructor light-player-constructor]
  {:board initial-board 
   :dark-move-fn (dark-player-constructor :dark)
   :light-move-fn (light-player-constructor :light)})

(defn make-basic-board [])

(defn make-move-or-fail [side move-fn board]
  {})

(defn game-step [{:keys [board dark-move-fn light-move-fn] :as game-stage}]
  (when-not (game-finished? board)
    (let [{:keys [dark-board dark-move-fn]} (make-move-or-fail :dark dark-move-fn board)
          {:keys [light-board light-move-fn]} (make-move-or-fail :light light-move-fn 
                                                                 (or dark-board board))
          new-stage {:board (or light-board dark-board board) 
                     :dark-move-fn dark-move-fn
                     :light-move-fn light-move-fn}]
      (cons new-stage (lazy-seq (game-step new-stage))))))

(defn create-standard-game [dark-player-constructor light-player-constructor]
  (let [game-stage (create-initial-game (make-basic-board) 
                                        dark-player-constructor 
                                        light-player-constructor)]
    (cons game-stage (lazy-seq (game-step game-stage)))))

