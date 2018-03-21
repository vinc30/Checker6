package checker6;


public class Player {
    private Piece[] holdingPiece;
    private Color color;

    public Player(CheckerBoard board, Color color) {
        this.color = color;
        holdingPiece = new Piece[board.getBoardSize()];
        int colorNumber = (color == Color.LIGHT ? 0 : 1);
        Position[] initialPosition = board.getInitialPositions()[colorNumber];
        for (int i = 0; i < board.getBoardSize(); i ++) {
            holdingPiece[i] = new Piece(initialPosition[i], this);
        }
    }
    public Piece[] getPieces() {
        return holdingPiece;
    }
    public Color getColor() {
        return color;
    }
}
