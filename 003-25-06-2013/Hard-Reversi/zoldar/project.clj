(defproject clothello "0.1.0-SNAPSHOT"
  :description "A game of Reversi/Othello for WeeklyWrongChallenge"
  :url "http://wereprobablywrong.so/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :profiles {:dev {:dependencies [[midje "1.6-alpha2"]]}}
  :main clothello.core)
