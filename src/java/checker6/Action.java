package checker6;

public class Action {
    private Piece piece;
    private Position newPosition;
    private boolean isJump;

    public Action(Piece piece, Position newPosition, boolean isJump) {
        this.piece = piece;
        this.newPosition = newPosition;
        this.isJump = isJump;
    }

    public Action() {
        this(null, null, false);
    }

    public Piece getPiece() {
        return piece;
    }

    public Position getNewPosition() {
        return newPosition;
    }

    public boolean isJump() {
        return isJump;
    }

    public void setAction(Action action) {
        this.piece = action.getPiece();
        this.newPosition = action.getNewPosition();
        this.isJump = action.isJump();
    }
}
