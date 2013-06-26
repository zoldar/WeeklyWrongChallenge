# clothello

A game of Reversi/Othello implemented as a CLI app in Clojure for Weekly Wrong Challenge.

## Usage

For now, there's only game's logic and basic random AI to play with. Human input should be also possible, but interactive version is yet to be written. There's no rendering at the moment. Eventually code will be split among multiple namespaces to not create such a mess.

Basic usage after firing up REPL:

    (use 'clothello.logic)
    (create-game classic-board create-random-ai-player create-random-ai-player)
    
To get just the winner for a given game:

    (-> (create-game classic-board create-random-ai-player create-random-ai-player)
        reverse first :board get-winner)

For statistical breakdown on games won by every side:

    (frequencies (repeatedly 100 #(-> (create-game classic-board create-random-ai-player create-random-ai-player) reverse first :board get-winner)))

This should result in something similar to:

    {:light 47, :dark 53}

Doing even only 100 runs seems to be slow but performance is a concern for later on I suppose...

UPDATE: I've added a greedy AI to the mix which proved to be quite easy. It can be used with `create-greedy-ai-player`.

    (frequencies (repeatedly 100 #(-> (create-game classic-board create-greedy-ai-player create-random-ai-player) reverse first :board get-winner)))
    
    {:dark 67, :light 33}

## License

Copyright © 2013 Adrian Gruntkowski

Distributed under the Eclipse Public License, the same as Clojure.
