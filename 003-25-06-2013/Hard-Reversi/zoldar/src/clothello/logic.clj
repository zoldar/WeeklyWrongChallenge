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

(defn- put-at [board [x y] piece]
  (assoc board (+ (* board-size x) y) piece))

(defn- get-at [board [x y]]
  (nth board (+ (* board-size x) y)))

;; Now this is entirely for the purpose of an exercise, but to make code
;; more idiomatic, I wanted to use more standard functions for board
;; interaction than get-at and put-at. 
;; The idea is that while interface for working with board is uniform and idiomatic,
;; the implementation may be easily swapped. For example, when using standard
;; map for the board where keys are vectors with coordinates and values
;; are keywords describing given board's field contents, nothing has to be
;; changed.
;;
;; Had to use `deftype`, because a record already implements Associative,
;; ILookup and many others.
(deftype FlatBoard [board]
  clojure.lang.Associative
  (containsKey [_ key]
    (let [[x y] key]
      (< (* x y) (* board-size board-size))))
  (entryAt [_ key]
    [key (get-at board key)])
  (assoc [this key val]
    (FlatBoard. (put-at board key val)))
  clojure.lang.ILookup
  (valAt [_ key]
    (get-at board key))
  (valAt [this key not-found]
    (if (.containsKey this)
      (get-at board key)
      not-found))
  Object
  (toString [_] (str board)))

;; Initial board for FlatBoard implementation, where the board is represented
;; as a flat vector of a length of board-size squared where every value represents
;; contents of a given board's field.
(def empty-board (FlatBoard. (vec (repeat (* board-size board-size) :empty))))

;; Offsets for moving around the board in form of vectors: [0 1] [1 1] [1 -1] etc.
(def offsets (for [x (range -1 2) y (range -1 2) 
                   :when (not-every? zero? [x y])] [x y]))

(defn within-boundaries? [point]
  "Check if a given is within board boundaries."
  (every? #(< -1 % board-size) point))

(defn board-seq [board]
  (for [x (range board-size) y (range board-size)] (get board [x y])))

(def classic-board
  "Standard initial board layout as stated in rules of a classic Reversi."
  (-> empty-board
      (assoc [3 3] :light)
      (assoc [4 4] :light)
      (assoc [3 4] :dark)
      (assoc [4 3] :dark)))

(defn generate-line [start offset]
  "Generate a series of points representing a line from a given
start point to the edge of the board along given offset."
  (take-while within-boundaries? 
              (iterate #(->> % (map + offset) vec) start)))

(defn get-flippable-points [side board [_ & line]]
  "Find all points containing pieces which can flipped along the given
line of points given a particular board state and side from which
move is made."
  (let [fields (map (partial get board) line)
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

(defn has-opposite-neighbors? [side board move]
  (let [opposite-side (if (= side :dark) :light :dark)
        neighbor-points (->> (map (partial map + move) offsets) (filter within-boundaries?))
        neighbor-pieces (map (partial get board) neighbor-points)]
    ((set neighbor-pieces) opposite-side)))

(defn valid-move? [side board move]
  (and move
       (= (get board move) :empty) 
       (has-opposite-neighbors? side board move)
       (seq (get-pieces-to-flip side board move))))

(defn make-move [side board move]
  "Given the move is valid, put the piece at given position and flip all
the opposite pieces eligible for flipping by game rules."
  (when (valid-move? side board move)
    (let [pieces-to-flip (get-pieces-to-flip side board move)]
      (reduce (fn [board position] (assoc board position side)) 
              (assoc board move side)
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
