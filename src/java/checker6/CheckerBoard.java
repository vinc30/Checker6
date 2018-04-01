package checker6;


public class CheckerBoard {
    private static final int BOARD_SIZE = 6;
    private static final Position[][] INITIAL_POSITIONS;
    
    public static int getBoardSize() {
        return BOARD_SIZE;
    }

    public static Position[][] getInitialPositions() {
        return INITIAL_POSITIONS;
    }

    private Piece[][] board;

    public CheckerBoard() {
        board = new Piece[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i ++) {
            for (int j = 0; j < BOARD_SIZE; j ++) {
                board[i][j] = null;
            }
        }
    }

    static {
        INITIAL_POSITIONS = new Position[2][BOARD_SIZE];
        INITIAL_POSITIONS[0][0] = new Position(0,1);
        INITIAL_POSITIONS[0][1] = new Position(0,3);
        INITIAL_POSITIONS[0][2] = new Position(0,5);
        INITIAL_POSITIONS[0][3] = new Position(1,0);
        INITIAL_POSITIONS[0][4] = new Position(1,2);
        INITIAL_POSITIONS[0][5] = new Position(1,4);
        
        INITIAL_POSITIONS[1][0] = new Position(4,1);
        INITIAL_POSITIONS[1][1] = new Position(4,3);
        INITIAL_POSITIONS[1][2] = new Position(4,5);
        INITIAL_POSITIONS[1][3] = new Position(5,0);
        INITIAL_POSITIONS[1][4] = new Position(5,2);
        INITIAL_POSITIONS[1][5] = new Position(5,4);
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

    public void eatPiece(Piece piece) {
        if (!isPositionEmpty(piece.getPosition())) {
            board[piece.getPosition().getX()][piece.getPosition().getY()] = null;
        } else {
            throw new IllegalArgumentException("CheckerBoard.eatPiece(): Phantom piece");
        }
    }

    public boolean isLegalPosition(Position position) {
        return position.getX() >= 0 && position.getX() < BOARD_SIZE &&
            position.getY() >= 0 && position.getY() < BOARD_SIZE;
    }

    @Override public String toString() {
        StringBuilder boardStatus = new StringBuilder(360);
        boardStatus.append("   0   1   2   3   4   5  \n");
        boardStatus.append(" -------------------------\n");
        for (int i = 0; i < BOARD_SIZE; i ++) {
            boardStatus.append(String.format("%d|", i));
            for (int j = 0; j < BOARD_SIZE; j ++) {
                boardStatus.append((board[i][j] == null ? "   " : String.format("%d%s ", board[i][j].getSerialNum(), board[i][j].toString()))).append("|");
            }
            boardStatus.append("\n");
            if (i != BOARD_SIZE - 1) {
                boardStatus.append(" |---+---+---+---+---+---|\n");
            }
        }
        boardStatus.append(" -------------------------\n");
        return boardStatus.toString();
    }
    public String toString(String boardStatusString, Position position) {

        StringBuilder boardStatus = new StringBuilder(boardStatusString);
        int idx = Utility.positionToIndex(position);
        if (getPieceByPosition(position) != null) {
            Player player = getPieceByPosition(position).getPlayer();
            boardStatus.setCharAt(idx, (player.getColor() == Color.LIGHT ? 'v' : '^'));
        } else {
            boardStatus.setCharAt(idx, '#');
        }
        return boardStatus.toString();
    }

    public String toString(Position position) {
         return toString(toString(), position);
    }

    public String toString(Position ... positions) {
        StringBuilder boardStatus = new StringBuilder(toString());
        for (Position position : positions) {
            boardStatus = new StringBuilder(toString(boardStatus.toString(), position));
        }
        return boardStatus.toString();
    }

}
