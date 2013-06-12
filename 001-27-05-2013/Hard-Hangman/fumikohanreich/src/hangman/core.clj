(ns hangman.core
  (:require [clojure.tools.cli :refer [cli optional]]
            [clojure.java.io :refer [reader]]))

(def secret (fn [] (with-open [r (reader "/usr/share/dict/words")]
                      (rand-nth (line-seq r)))))

(def wildcard "_")

(def hangman-drawing 
  {0 (str "\n8 turns remaining\n")
   1 (str "\n\n\n\n\n |" "\n |" "\n |" "\n |" 
              "\n\n 7 turns remaining\n")
   2 (str "\n\n |" "\n |" "\n |" "\n |" "\n |" "\n |" "\n |"
              "\n\n 6 turns remaining\n")
   3 (str " _______" "\n |" "\n |" "\n |" "\n |" "\n |" "\n |" "\n |" 
              "\n\n 5 turns remaining\n")
   4 (str " _______" "\n |     |" "\n |     |" "\n |" "\n |" "\n |" "\n |" "\n |" 
              "\n\n 4 turns remaining\n")
   5 (str " _______" "\n |     |" "\n |     |" "\n |     O" "\n |" "\n |" "\n |" "\n |" 
              "\n\n 3 turns remaining\n")
   6 (str " _______" "\n |     |" "\n |     |" "\n |     O" "\n |    /\\" "\n |" "\n |" "\n |" 
              "\n\n 2 turns remaining\n")
   7 (str " _______" "\n |     |" "\n |     |" "\n |     O" "\n |    /\\" "\n |     |" "\n |" "\n |" 
              "\n\n only 1 more turn, that's it!\n")
   8 (str " _______\n" " |     |\n" " |     |\n" " |     O\n" " |     /\\" "\n |     |" "\n |     /\\" "\n |")})

;; (defn- not-wildcard? [c]
;;   (not= c wildcard))

;(defn f [c1 c2]
; (cond (not-wildcard? c1) c1 
;       (not-wildcard? c2) c2 
;       :else wildcard))
;(map #(f %1 %2) s1 s2)

;; couldn't get the above to work, hence below...

(defn- mask
  "Given two strings (of same length), 
   return a string which each character in the same position is replaced 
   with one from the second string iff one from the first string is wildcard.
   (e.g. 'a__' and '_b_' will return 'ab_' where '_' is a wildcard)" 
  [s1 s2]
 (->> (map #(if (= %1 (int (first wildcard))) %2 %1) (map int s1) (map int s2))
      (map char)
      (apply str)))

(defn- unveil
  "Given secret word, already unveiled word, and a char,
   return a newly unveiled word."
  [secret unveiled char]
  (->> (map #(if (= % char) char wildcard) secret)
       (apply str)
       (mask unveiled)))

(defn- prompt
  "Given a string, return a user prompt."
  [s]
  (do (flush) (println s)))

;; could do (match? ...) in the second one, but = is fast/simple enough.
(defn- match?
  "Given user input, secret word, and unveiled word,
   return true if user guessed secret word; false, otherwise."
  ([secret unveiled] (= secret unveiled))
  ([input secret unveiled]
  (or (= secret input) (= secret unveiled))))

(defn- read-guess
  "Given secret word, unveiled word (partially guessed secret word), 
   and turns as a positive integer,
   println hangman drawing if previously missed,
   prompt a user, and read the next line."
  [secret unveiled turns]
  (do (when-not (match? secret unveiled) (println (hangman-drawing turns)))
      (prompt (str "The unveiled word is " unveiled  ". Please enter again."))
      (read-line)))

(defn- hangman-game
  "Given user input, secret word, already unveiled word,
   and turns as a positive integer,
   returns true when a user won the game; false, otherwise,
   while repeatedly asking the user until his/her guessess the secret word."
  [input secret unveiled turns]
  (let [first-char (first input)
        unveiled   (unveil secret unveiled (first input))
        match?     (match? input secret unveiled)
        turns      (if (some #(= first-char %) secret) turns (inc turns))]
    (if (or match? (>= turns (dec (count hangman-drawing))))
      match?
      (hangman-game (read-guess secret unveiled turns) secret unveiled turns))))

(defn -main [& args]
  (let [options (cli args (optional ["-secret" "secret word or phrase" :default (secret)]))
        secret  (options :secret)
        hint    (apply str (repeat (count secret) wildcard))] 

    (prompt (str " --- Welcome to Hangman Game ---\n\n"
                 "The secret word is " hint "\n"
                 "Please enter a word or a character.\n"))
    
    (if (hangman-game (read-line) secret hint 0)
      (prompt (str "You won! " secret))
      (prompt (str (second (last hangman-drawing)) 
                   "\n\nSorry, you lost :(  The secret word was " secret ". Try again :)")))))
