package checker6;


public class Piece {
    private Position position;
    private Player player;
    private int serialNum;

    public Piece(Position position, Player player, int serialNum) {
        this.position = position;
        this.player = player;
        this.serialNum = serialNum;
    }
    public Position getPosition() {
        return position;
    }

    public Player getPlayer() {
        return player;
    }

    public int getSerialNum() {
        return serialNum;
    }
    
    public Position getPositionAfterMove(Move move) {
        
        /** 
         * Light player moving down, dark moving up
         */
        int direction = player.getColor() == Color.LIGHT ? 1 : -1;
        switch (move) {
            case LEFT:
    	        return new Position(position.getX() + direction, position.getY() - 1); 
            case RIGHT:
                return new Position(position.getX() + direction, position.getY() + 1);
            case LEFTJUMP:
                return new Position(position.getX() + 2 * direction, position.getY() - 2);
            case RIGHTJUMP:
                return new Position(position.getX() + 2 * direction, position.getY() + 2);
            default:
                throw new IllegalArgumentException("getPositionAfterMove(): Unrecognizable move");
        }
    }
    @Override public String toString() {
        return player.getColor() == Color.LIGHT ? "O" : "X";
    }
}

