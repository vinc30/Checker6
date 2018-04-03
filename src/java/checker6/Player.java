package checker6;


import java.util.ArrayList;

public class Player {
    private Piece[] holdingPiece;
    private Color color;
    private int remainingPieces;
    private CheckerBoard board;
    private boolean human;

    public Player(CheckerBoard board, Color color, boolean human) {
        this.board = board;
        this.color = color;
        this.human = human;
        holdingPiece = new Piece[CheckerBoard.getBoardSize()];
        int colorNumber = (color == Color.LIGHT ? 0 : 1);
        Position[] initialPosition = CheckerBoard.getInitialPositions()[colorNumber];
        for (int i = 0; i < CheckerBoard.getBoardSize(); i ++) {
            holdingPiece[i] = new Piece(initialPosition[i], this, i);
        }
        remainingPieces = initialPosition.length;
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
    }

    public Player() {
        this.board = null;
        this.color = Color.DARK;
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
    public void gotEaten(int pieceNum) {
        if (holdingPiece[pieceNum] != null) {
            board.eatPiece(holdingPiece[pieceNum]);
            holdingPiece[pieceNum] = null;
            remainingPieces --;
        } else {
            throw new IllegalArgumentException("gotEaten(): piece already eaten");
        }
    }

    public void updatePiece(int serialNum, Position newPosition) {
        if (holdingPiece[serialNum] != null) {
            Position oldPosition = holdingPiece[serialNum].getPosition();
            holdingPiece[serialNum] = new Piece(newPosition, this, serialNum);
            board.movePiece(holdingPiece[serialNum], oldPosition);

        } else {
            throw new IllegalArgumentException("updatePiece(): Moving eaten piece");
        }
    }
    /*
    public Position[] getAllPiecesPosition() {

        ArrayList<Position> positions = new ArrayList<>();

        for (Piece piece : holdingPiece) {
            if (piece != null) {
                positions.add(piece.getPosition());
            }
        }
        return positions.size() == 0 ? null : positions.toArray(new Position[positions.size()]);
    }
    */

    public Color getColor() {
        return color;
    }

    public CheckerBoard getBoard() {
        return board;
    }

    public boolean isHuman() {
        return human;
    }

}
