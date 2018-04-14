package checker6_gui;

/**
 * Author: Yu Jheng Fang
 * Date: Apr 13 2018
 */

import java.util.ArrayList;

/**
 * Each piece on checker board is represented in a Piece class
 * Piece class the following fields:
 *     position: the position of this piece
 *     player: the player who own this piece
 *     serialNum: the serial number of this piece, which is the
 *                index of this piece among all pieces holden by
 *                the same player
 *     piece_image_path: image the path to Light/Dark piece
 */

public class Piece {
    private Position position;
    private Player player;
    private int serialNum;
    private String piece_image_path;

    public Piece(Position position, Player player, int serialNum) {
        this.position = position;
        this.player = player;
        this.serialNum = serialNum;
        if (player.getColor() == PlayerColor.LIGHT) {
            // White player
            this.piece_image_path = "assets/light.png";
        } else {
            // Black player
            this.piece_image_path = "assets/dark.png";
        }
    }

    public Piece() {
        this.position = null;
        this.player = null;
        this.serialNum = -1;
    }

    public Piece(Piece piece, Player player) {
        this(piece.getPosition(), player, piece.getSerialNum());
    }

    /**
     * Setter, set the piece image path of this piece
     * @param piece_image_path
     */
    public void setPiece_image_path(String piece_image_path){
        this.piece_image_path = piece_image_path;
    }

    /**
     * Getter, return piece image path of this piece
     * @return image path of this piece
     */
    public String getPiece_image_path(){
        return this.piece_image_path;
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

    /**
     * return a ArrayList<Position> of possible moves, which
     * refers to the positions if this piece goes LEFT,
     * RIGHT or jumps LEFT, RIGHT
     */

    public ArrayList<Position> getPossibleMoveCoordinate() {
        ArrayList<Position> legalMoves = new ArrayList<>();
        legalMoves.add(getPositionAfterMove(Move.LEFTJUMP));
        legalMoves.add(getPositionAfterMove(Move.RIGHTJUMP));
        legalMoves.add(getPositionAfterMove(Move.LEFT));
        legalMoves.add(getPositionAfterMove(Move.RIGHT));
        // return legalMoves.size() == 0 ? null : legalMoves.toArray(new Position[legalMoves.size()]);
        return legalMoves.size() == 0 ? null : legalMoves;
    }

    /**
     * get the position of the piece after a 'move'
     * There are four possible moves for each piece
     * LEFT, RIGHT: simply move piece forward to adjacent diagonal
     *              position.
     *              E.g. O denotes the piece, L = LEFT move, R = RIGHT move
     *              -------------
     *              | L |   | R |
     *              |---+---+---+
     *              |   | O |   |
     *              |---+---+---|
     * LEFTJUMP, RIGHTJUMP: capture moves, which a piece jumps over another
     *                      rival piece. The rival piece is removed after a
     *                      jump move.
     *                      E.g. LJ = LEFTJUMP, RJ = RIGHTJUMP, O = piece
     *                           X = rival's piece
     *           ---------------------
     *           | LJ|   |   |   | RJ|
     *           |---+---+---+---+---|
     *           |   | X |   | X |   |
     *           |---+---+---+---+---|
     *           |   |   | O |   |   |
     *           |---+---+---+---+---|
     *                     |
     *                     v  after left jump
     *           ---------------------
     *           | O |   |   |   |   |
     *           |---+---+---+---+---|
     *           |   |   |   | X |   |
     *           |---+---+---+---+---|
     *           |   |   |   |   |   |
     *           |---+---+---+---+---|
     *
     *
     *
     * @param move
     * @return Position
     */
    public Position getPositionAfterMove(Move move) {

        /**
         * Light(human) player moving up, dark(AI) moving down
         */
        int direction = player.getColor() == PlayerColor.LIGHT ? 1 : -1;
        switch (move) {
            case LEFT:
                return new Position(position.getX() - 1, position.getY() + direction);
            case RIGHT:
                return new Position(position.getX() + 1, position.getY() + direction);
            case LEFTJUMP:
                return new Position(position.getX() - 2, position.getY() + 2 * direction);
            case RIGHTJUMP:
                return new Position(position.getX() + 2, position.getY() + 2  * direction);
            default:
                throw new IllegalArgumentException("getPositionAfterMove(): Unrecognizable move");
        }
    }

    @Override public String toString() {
        return player.getColor() == PlayerColor.LIGHT ? "@" : "X";
    }
}

