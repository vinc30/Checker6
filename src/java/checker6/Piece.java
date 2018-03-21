package checker6;


public class Piece {
    private Position position;
    private Player player;

    public Piece(Position position, Player player) {
        this.position = position;
        this.player = player;
    }
    public Position getPosition() {
        return position;
    }

    public Player getPlayer() {
        return player;
    }
    @Override public String toString() {
        return player.getColor() == Color.LIGHT ? "O" : "X";
    }
}

