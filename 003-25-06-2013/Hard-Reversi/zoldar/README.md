# clothello

A game of Reversi/Othello implemented as a CLI app in Clojure for Weekly Wrong Challenge.

## Usage

The game has a basic CLI interface. To start the game, you have to issue a following command:

    lein run [dark-player] [light-player]

Currently available choices for players are: `human`, `random` and `greedy`.

Some examples:

    lein run human human

    lein run human greedy

    lein run random greedy

There is one caveat when playing in interactive (human) mode - although invalid input is rejected, the invalid move is just silently discarded and the game carries on as if there was no move made in a given turn.

It's also possible to play the game directly from REPL. Basic usage after firing up REPL:

    (use 'clothello.logic)
    (create-game classic-board create-random-ai-player create-random-ai-player)
    
To get just the winner for a given game (nil result will mean a tie):

    (-> (create-game classic-board create-random-ai-player create-random-ai-player)
        reverse first :board get-winner)

For statistical breakdown on games won by every side:

    (frequencies (repeatedly 100 #(-> (create-game classic-board create-random-ai-player create-random-ai-player) reverse first :board get-winner)))

This should result in something similar to:

    {:light 47, :dark 53}


### Creating a custom AI player

The player constructor which is passed to the game's constructor
must accept side for wich to play as an argument.
Its return value must be a move making function accepting a board as an
argument and returning a map with two keys as a result:
    - `:move` associated with a move to make in a form of a vector of two 
  integers
    - `:move-fn` associated with a function fulfilling the same contract

There's a convenience constructor in the player namespace - create-ai-player
along with ai-player, preserving a history of board states in a vector. 
It's entirely optional.

Player AI definitions (constructors along with all auxiliary functions)
must be put in a namespace under `clothello.ai.*`. Constructors must be 
registered inside those namespaces to be available
in the game. Registration is made with `register-player` function.

The example AI implementations are available under `clothello.ai.simple`.

## License

Copyright © 2013 Adrian Gruntkowski

Distributed under the Eclipse Public License, the same as Clojure.
