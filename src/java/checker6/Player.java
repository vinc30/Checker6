package checker6;


public class Player {
    private Piece[] holdingPiece;
    private Color color;
    private int remainingPieces;
    private CheckerBoard board;

    public Player(CheckerBoard board, Color color) {
        this.board = board;
        this.color = color;
        holdingPiece = new Piece[board.getBoardSize()];
        int colorNumber = (color == Color.LIGHT ? 0 : 1);
        Position[] initialPosition = board.getInitialPositions()[colorNumber];
        for (int i = 0; i < board.getBoardSize(); i ++) {
            holdingPiece[i] = new Piece(initialPosition[i], this, i);
        }
        remainingPieces = initialPosition.length;
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

    public Color getColor() {
        return color;
    }
}
