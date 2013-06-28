(ns clothello.player
  (:require [clojure.tools.namespace.find :as ns-find]))

;; Creating a custom AI player
;; ---------------------------
;; 
;; The player constructor which is passed to the game's constructor
;; must accept side for wich to play as an argument.
;; Its return value must be a move making function accepting a board as an
;; argument and returning a map with two keys as a result:
;; - :move associated with a move to make in a form of a vector of two 
;;   integers
;; - :move-fn associated with a function fulfilling the same contract
;;
;; There's a convenience constructor in the current namespace - create-ai-player
;; along with ai-player, preserving a history of board states in a vector. 
;; It's entirely optional.
;;
;; Player AI definitions (constructors along with all auxiliary functions)
;; must be put in a namespace under `clothello.ai.*`.
;; Constructors must be registered inside those namespaces to be available
;; in the game. Registration is made with `register-player` function.
;;
;; The example AI implementations are available under `clothello.ai.simple`


;; Holds a list of player constructors where key is a string
;; representing the name of the player type, na value is the
;; constructor function.
(def ^:dynamic *players* (atom {}))

(defn load-thirdparty-ai []
  "A bit of a namespace voodoo to autoload third party-AI.
An assumption is made, that all ai implementations are placed
in namespaces under `clothello.ai.*`."
  (let [namespaces (ns-find/find-namespaces-in-dir 
                    (clojure.java.io/file "src/clothello/ai"))]
    (doseq [namespace namespaces] (require namespace))))

(defn get-players []
  "For retrieval of registered players."
  @*players*)

(defn register-player [name constructor-fn]
  "Registration function that"
  (swap! *players* assoc name constructor-fn))

(defn ai-player [side history decision-fn board]
  "AI player constructor helper holding the history of board states
across a single game."
  (let [move-fn (partial ai-player side (conj history board) decision-fn)]
    {:move (decision-fn side history board) :move-fn move-fn}))

(defn create-ai-player [side decision-fn]
  "AI player constructor. Requires a decision function accepting arguments
in the given order: side for which decision is made, history of board changes 
and current board state. The function has to return a move to make in a form
of a vector of two integer coordinates."
  (partial ai-player side [] decision-fn))

(defn human-player [side input-seq board]
  "Human player constructor helper."
  (let [[current-input input-seq] ((juxt first rest) input-seq)] 
    {:move current-input :move-fn (partial human-player side input-seq)}))

(defn create-human-player [side input-seq]
  "Human player constructor. Accepts a sequence of inputs in a form of vectors
of two integer coordinates. Intended for use with a lazy sequence of external
user input."
  (partial human-player side input-seq))

;; Call must be made at the end because functions from that namespace
;; must be available before loading. Beware of circular imports...
(load-thirdparty-ai)
