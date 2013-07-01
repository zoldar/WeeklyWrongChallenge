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

(def board-size 8)

;; Board has a form of a flat vector which is `board-size` squared long. 
;; The valid values in the vector are :dark, :light and :empty.
(def empty-board (vec (repeat (* board-size board-size) :empty)))

;; Offsets for moving around the board in form of vectors: [0 1] [1 1] [1 -1] etc.
(def offsets (for [x (range -1 2) y (range -1 2) 
                   :when (not-every? zero? [x y])] [x y]))

(defn within-boundaries? [point]
  "Check if a given is within board boundaries."
  (every? #(< -1 % board-size) point))

(defn put-at [board piece [x y]]
  (assoc board (+ (* board-size x) y) piece))

(defn get-at [board [x y]]
  (nth board (+ (* board-size x) y)))

(defn board-seq [board]
  (for [x (range board-size) y (range board-size)] (get-at board [x y])))

(def classic-board
  "Standard initial board layout as stated in rules of a classic Reversi."
  (-> empty-board
      (put-at :light [3 3])
      (put-at :light [4 4])
      (put-at :dark [3 4])
      (put-at :dark [4 3])))

(defn generate-line [start offset]
  "Generate a series of points representing a line from a given
start point to the edge of the board along given offset."
  (take-while within-boundaries? 
              (iterate #(->> % (map + offset) vec) start)))

(defn get-flippable-points [side board [_ & line]]
  "Find all points containing pieces which can flipped along the given
line of points given a particular board state and side from which
move is made."
  (let [fields (map (partial get-at board) line)
        flip-check-fn (fn [position] (and (not= side position) 
                                          (not= :empty position)))
        flip-candidates (take-while flip-check-fn fields)
        [last-after-flips & _] (drop-while flip-check-fn fields)]
    (when (and (seq flip-candidates) (= last-after-flips side))
      (take (count flip-candidates) line))))

(defn get-pieces-to-flip [side board move]
  "Generate lines of points in all possible directions from a given
starting position and return a collection of all points containing
pieces to flip."
  (let [lines (map (partial generate-line move) offsets)]
    (mapcat (partial get-flippable-points side board) lines)))

(defn valid-move? [side board move]
  (and move
       (= (get-at board move) :empty) 
       (seq (get-pieces-to-flip side board move))))

(defn make-move [side board move]
  "Given the move is valid, put the piece at given position and flip all
the opposite pieces eligible for flipping by game rules."
  (when (valid-move? side board move)
    (let [pieces-to-flip (get-pieces-to-flip side board move)]
      (reduce (fn [board position] (put-at board side position)) 
              (put-at board side move) 
              pieces-to-flip))))

(defn get-valid-moves [side board]
  "Get all valid moves given a board state and side that makes the move."
  (let [coordinates (for [x (range board-size) y (range board-size)] [x y])]
    (filter (partial valid-move? side board) coordinates)))

(defn game-finished? [board]
  "Check if any side can make a valid move."
  (empty? (mapcat get-valid-moves [:dark :light] (repeat board))))

(defn get-winner [board]
  "Retrieve a winner given number of the pieces on the board. In case of 
a tie, return nil."
  (when (game-finished? board) 
    (let [{:keys [dark light] :or {dark 0 light 0}} 
          (->> board 
               board-seq
               (remove (partial = :empty))
               frequencies)]
      (cond (> dark light) :dark
            (> light dark) :light
            :else nil))))

(defn create-initial-game [initial-board 
                           dark-player-constructor 
                           light-player-constructor 
                           first-turn]
  "Initial game state creation helper."
  {:board initial-board 
   :dark {:move-fn (dark-player-constructor :dark)}
   :light {:move-fn (light-player-constructor :light)}
   :turn first-turn})

(defn make-move-or-fail [move-fn side board]
  "Given player's move function, side which he's playing on and board state,
return new board state (or nil if move is incorrect) and new move function."
  (let [{:keys [move move-fn]} (move-fn board)]
    {:new-board (make-move side board move) :move-fn move-fn}))

(defn game-step [{:keys [board turn] :as game-stage}]
  "Create a new game state, alternating the sides with each turn until the game
is finished."
  (when-not (game-finished? board)
    (let [side turn
          flipped-side (if (= side :dark) :light :dark)
          move-fn (-> game-stage side :move-fn)
          {:keys [new-board move-fn]} (make-move-or-fail move-fn side board)
          new-stage (-> game-stage 
                        (assoc :board (or new-board board))
                        (assoc :turn flipped-side)
                        (assoc-in [side :move-fn] move-fn))]
      (cons new-stage (lazy-seq (game-step new-stage))))))

(defn create-game [initial-board dark-player-constructor light-player-constructor]
  "Game sequence constructor."
  (let [game-stage (create-initial-game initial-board 
                                        dark-player-constructor 
                                        light-player-constructor
                                        :dark)]
    (cons game-stage (lazy-seq (game-step game-stage)))))
