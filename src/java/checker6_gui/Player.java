package checker6_gui;

/**
 * Author: Yu Jheng Fang
 * Date: Apr 13 2018
 */

/**
 * The Player class holds information of each player, for the instance fields in Player
 *
 *     holdingPiece: fixed length array of the pieces holden by one player
 *                   if a piece is eaten, it will be null in holdingPiece array
 *     color: color of a player, where LIGHT is human player, DARK is AI player
 *     remainingPieces: the number of remaining pieces of a player
 *     board: the board where players are playing
 *     human: boolean variable to indicate if this player is human
 *     score: score is currently defined as the number of rival pieces captured
 *     name: name of the player
 *
 */
public class Player {
    private Piece[] holdingPiece;
    private PlayerColor color;
    private int remainingPieces;
    private CheckerBoard board;
    private boolean human;
    private int score;
    private String name;

    public Player(CheckerBoard board, PlayerColor color, boolean human) {
        this.board = board;
        this.color = color;
        this.human = human;
        holdingPiece = new Piece[CheckerBoard.getBoardSize()];
        int colorNumber = (color == PlayerColor.LIGHT ? 0 : 1);
        Position[] initialPosition = CheckerBoard.getInitialPositions()[colorNumber];
        for (int i = 0; i < CheckerBoard.getBoardSize(); i ++) {
            holdingPiece[i] = new Piece(initialPosition[i], this, i);
        }
        remainingPieces = initialPosition.length;
        score = 0;
        name = color == PlayerColor.LIGHT ? "LIGHT" : "DARK";
    }

    public Player(CheckerBoard board, Player player) {
        this.board = board;
        this.color = player.getColor();
        this.human = player.isHuman();
        holdingPiece = new Piece[CheckerBoard.getBoardSize()];
        for (Piece piece : player.getPieces()) {
            if (piece != null) {
                Piece insertingPiece = new Piece(piece.getPosition(), this, piece.getSerialNum());
                holdingPiece[insertingPiece.getSerialNum()] = insertingPiece;
            }
        }
        remainingPieces = player.getRemainingPieces();
        score = player.getScore();
    }

    public Player() {
        this.board = null;
        this.color = PlayerColor.DARK;
        this.human = false;
        holdingPiece = null;
        remainingPieces = -1;
    }

    public Piece[] getPieces() {
        return holdingPiece;
    }

    public Piece getPieces(int idx) {
        return holdingPiece[idx];
    }
    public int getRemainingPieces() {
        return remainingPieces;
    }

    /**
     * if player A eat a piece of player B
     * B will revoke gotEaten() and remove this piece from its
     * holdingPiece[] and from the board
     * @param pieceNum
     */
    public void gotEaten(int pieceNum) {
        if (holdingPiece[pieceNum] != null) {
            board.removePiece(holdingPiece[pieceNum]);
            holdingPiece[pieceNum] = null;
            remainingPieces --;
        } else {
            throw new IllegalArgumentException("gotEaten(): piece already eaten");
        }
    }

    /**
     * assign a piece to new position and adjust the board coordinately
     */
    public void updatePiece(int serialNum, Position newPosition) {
        if (holdingPiece[serialNum] != null) {
            Position oldPosition = holdingPiece[serialNum].getPosition();
            holdingPiece[serialNum] = new Piece(newPosition, this, serialNum);
            board.movePiece(holdingPiece[serialNum], oldPosition);

        } else {
            throw new IllegalArgumentException("updatePiece(): Moving eaten piece");
        }
    }


    public PlayerColor getColor() {
        return color;
    }

    public CheckerBoard getBoard() {
        return board;
    }

    public boolean isHuman() {
        return human;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore() {
        score ++;
    }

    @Override public String toString() {
        return (getColor() == PlayerColor.DARK) ? "Dark" : "Light";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void resetScore() {
        score = 0;
    }
}
