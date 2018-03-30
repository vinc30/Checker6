package checker6;

public class Action {
    private final Piece piece;
    private final Position newPosition;

    public Action(Piece piece, Position newPosition) {
        this.piece = piece;
        this.newPosition = newPosition;
    }

    public Piece getPiece() {
        return piece;
    }

    public Position getNewPosition() {
        return newPosition;
    }
}
