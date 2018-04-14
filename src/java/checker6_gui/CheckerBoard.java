package checker6_gui;

/**
 * Author: Yu Jheng Fang
 * Date: Apr 13 2018
 */

/**
 *
 * Construct chess board given width and height
 *
 *      Coordinate system
 *
 *      +
 *      |
 *      |
 *      |
 *      |
 *      (0, 0) -------->  +
 *
 *
 */
public class CheckerBoard {

    private static final int BOARD_SIZE = 6;
    private static final Position[][] INITIAL_POSITIONS;

    static {
        INITIAL_POSITIONS = new Position[2][BOARD_SIZE];
        // playerLight's pieces
        INITIAL_POSITIONS[0][0] = new Position(0,0);
        INITIAL_POSITIONS[0][1] = new Position(1,1);
        INITIAL_POSITIONS[0][2] = new Position(2,0);
        INITIAL_POSITIONS[0][3] = new Position(3,1);
        INITIAL_POSITIONS[0][4] = new Position(4,0);
        INITIAL_POSITIONS[0][5] = new Position(5,1);
        // playerDark's pieces
        INITIAL_POSITIONS[1][0] = new Position(0,4);
        INITIAL_POSITIONS[1][1] = new Position(1,5);
        INITIAL_POSITIONS[1][2] = new Position(2,4);
        INITIAL_POSITIONS[1][3] = new Position(3,5);
        INITIAL_POSITIONS[1][4] = new Position(4,4);
        INITIAL_POSITIONS[1][5] = new Position(5,5);
    }

    public static int getBoardSize() {
        return BOARD_SIZE;
    }
    public static Position[][] getInitialPositions() {
        return INITIAL_POSITIONS;
    }

    private Piece[][] board;
    protected final int width;           // the width of chess board
    protected final int height;          // the height of chess board
    protected int turns;  // game turn

    public CheckerBoard() {
        board = new Piece[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i ++) {
            for (int j = 0; j < BOARD_SIZE; j ++) {
                board[i][j] = null;
            }
        }
        width = BOARD_SIZE;
        height = BOARD_SIZE;
        turns = 0;
    }

    private void putPiece(Piece piece) {
        Position position = piece.getPosition();
        if (isPositionEmpty(position)) {
            board[position.getX()][position.getY()] = piece;
        } else {
            throw new IllegalArgumentException("CheckerBoard.putPiece(): Position taken");
        }
    }

    public void putPiece(Piece... pieces) {
        for (Piece piece : pieces) {
            if (piece != null) {
                putPiece(piece);
            }
        }
    }

    public Piece getPieceByPosition(Position position) {
        return board[position.getX()][position.getY()];
    }

    public Piece getPieceByPosition(int i, int j) {
        return board[i][j];
    }

    public boolean isPositionEmpty(Position position) {
        return board[position.getX()][position.getY()] == null;
    }

    public void movePiece(Piece piece, Position oldPosition) {
        if (isPositionEmpty(piece.getPosition())) {
            board[oldPosition.getX()][oldPosition.getY()] = null;
            board[piece.getPosition().getX()][piece.getPosition().getY()] = piece;
        } else {
            throw new IllegalArgumentException("CheckerBoard.movePiece(): Position already taken");
        }
    }

    public void removePiece(Piece piece) {
        if (!isPositionEmpty(piece.getPosition())) {
            board[piece.getPosition().getX()][piece.getPosition().getY()] = null;
        } else {
            throw new IllegalArgumentException("CheckerBoard.eatPiece(): Phantom piece");
        }
    }

    /**
     * If a position is within the range of a board
     * @param position
     * @return
     */
    public boolean isLegalPosition(Position position) {
        return position.getX() >= 0 && position.getX() < BOARD_SIZE &&
                position.getY() >= 0 && position.getY() < BOARD_SIZE;
    }

    /**
     * Setter: set turns
     * @param turns
     */
    public void setTurns(int turns){
        this.turns = turns;
    }

    /**
     * Increment turns by 1
     */
    public void incrementTurns(){
        this.turns++;
    }

    /**
     * Getter: get turns
     * @return turns of the game
     */
    public int getTurns(){
        return this.turns;
    }

    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }
}
