# Reversi

Your task, should you choose to attempt it, is to build a command line [Reversi](http://en.wikipedia.org/wiki/Reversi) game.

Feel free to pair, discuss on the ML or anything else really.

Reversi rules in short bullet points:
- the game takes place on a square board of dimensions 8x8 squares
- two players take part in the game, represented by light and dark pieces
- the standard starting position is in the group of central four squares 
  which are filled with 4 pieces - 2 pieces per player in a following layout
      (D - dark, L - light)
    L | D
    -----
    D | L
- player with dark pieces moves first
- players in turns place a piece with their color on the board 
  in such a position that there exists at least one straight (horizontal, 
  vertical, or diagonal) occupied line between the new piece and another 
  piece of their color, with one or more contiguous pieces of opposite color 
  between them
- after placing the piece, all pieces of opposite color lying on a straight line 
  between the new piece and any anchoring piece of the given player's color are
  changed to the same color
- a valid move is one where at least one piece is reversed
- if one player cannot make a valid move, play passes back to the other player
- when neither player can make a valid move, the game ends
- the player with the most pieces on the board at the end of the game win
