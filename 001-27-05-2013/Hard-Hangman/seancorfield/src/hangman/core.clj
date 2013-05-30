;; copyright (c) 2013 sean corfield

(ns hangman.core
  (:require [clojure.string :refer [join]])
  (:gen-class))

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

(defn- draw-gallows
  "Draw the current state of the gallows."
  [n]
  (println (nth full-gallows n)))

(defn- word-slots
  "Given a word and the current set of guesses, return the word with
   _ for not yet known letters."
  [word guesses]
  (join " " (map #(get guesses % \_) word)))

(defn- correct?
  "Given a word and a guess, return truth if the guess is correct."
  [word guess]
  ((set word) guess))

(defn- load-dict
  "Load the unix dictionary as a sequence of words. We use only lowercase words
   that are at least five characters long and no more than nine."
  []
  (->> (slurp "/usr/share/dict/words")
       (re-seq #"[a-z]+")
       (filter #(< 4 (.length ^String %)))
       (filter #(> 10 (.length ^String %)))))

(def ^:private words
  (delay (load-dict)))

(defn- choose-word
  "Randomly generate a word."
  []
  (rand-nth @words))

(defn- guess
  "Let the user enter a guess and return its value (a character)."
  []
  (let [line (read-line)]
    (if (= "quit" line)
      (System/exit 0)
      (first line))))

(defn -main
  "The game of Hangman"
  [& args]
  (loop [word (choose-word)]
    (loop [guesses #{}
           correct #{}
           bad-guesses 0]
      (draw-gallows bad-guesses)
      (when (seq guesses)
        (println "Guesses so far:" (join " " (sort guesses))))
      (println (word-slots word guesses))
      (cond  (= (count correct) (count (set word)))
             (println "Congratulations! You guessed my word!\nLet's play again!")
             (= (inc bad-guesses) (count full-gallows))
             (println "You're hanged! My word was:" word "\nLet's play again!")
             :else
             (do
               (println "Enter your guess or type quit:")
               (let [letter (guess)]
                 (if (correct? word letter)
                   (do
                     (println "Correct!")
                     (recur (conj guesses letter)
                            (conj correct letter)
                            bad-guesses))
                   (do
                     (println "Nope!")
                     (recur (conj guesses letter)
                            correct
                            (if (guesses letter) bad-guesses (inc bad-guesses)))))))))
    (recur (choose-word))))
