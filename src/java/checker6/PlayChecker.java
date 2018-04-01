package checker6;

import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PlayChecker {

    private static final int SEARCH_DEPTH_LIMIT = 10;

    public static void main(String[] args) {

        PlayChecker game = new PlayChecker();

        // to-do: Let user choose color
        // to-do: Let user set AI level
        // to-do: GUI

        Player firstPlayer = game.getPlayer(Color.LIGHT);
        Player secondPlayer = game.getPlayer(Color.DARK);

        while (!game.gameEnded()) {
            if (game.getNumberOfLegalMoves(firstPlayer) > 0) {
                game.move(firstPlayer);
                System.out.print(game.getBoard().toString());
            } 
            if (game.getNumberOfLegalMoves(secondPlayer) > 0) {
                game.move(secondPlayer);
                System.out.print(game.getBoard().toString());
            }
        }

        System.out.print("GAME OVER\n");
        System.out.print(game.getBoard().toString());
        System.out.printf("Winner: %s\n", game.getResult());
    }
    
    private CheckerBoard board;
    private Player playerLight;
    private Player playerDark;

    private PlayChecker() {
        board = new CheckerBoard();
        playerLight = new Player(board, Color.LIGHT, true);
        playerDark = new Player(board, Color.DARK, false);  // this is AI
        board.putPiece(playerLight.getPieces());
        board.putPiece(playerDark.getPieces());
    }

    private PlayChecker(PlayChecker game) {
        board = new CheckerBoard();
        playerLight = new Player(board, game.getPlayer(Color.LIGHT));
        playerDark = new Player(board, game.getPlayer(Color.DARK));
        board.putPiece(playerLight.getPieces());
        board.putPiece(playerDark.getPieces());
    }

    public String getResult() {
        if (playerDark.getRemainingPieces() == playerLight.getRemainingPieces()) {
            return "DRAW";
        }
        return playerDark.getRemainingPieces() > playerLight.getRemainingPieces() ?
            "DARK" : "LIGHT";
    }

    private void move(Player player) {
        Action input = new Action();
        if (player.isHuman()) {
            input = getValidInput(player);
        } else {
            input = alphaBetaSearch(this);
        }
        if (input.isJump()) {
            Piece eatenPiece = board.getPieceByPosition(new Position((input.getPiece().getPosition().getX() + input.getNewPosition().getX()) / 2,
                    (input.getPiece().getPosition().getY() + input.getNewPosition().getY()) / 2));
            eatenPiece.getPlayer().gotEaten(eatenPiece.getSerialNum());
        }
        player.updatePiece(input.getPiece().getSerialNum(), input.getNewPosition());
    }

    private Action alphaBetaSearch(PlayChecker game) {
        UtilityAndAction result = new UtilityAndAction();

        result = maxValue(game, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, SEARCH_DEPTH_LIMIT);
        return result.getAction();
    }

    private UtilityAndAction maxValue(PlayChecker game, double alpha, double beta, int searchDepthLimit) {

        if (game.gameEnded() || searchDepthLimit == 0) {
            return new UtilityAndAction(null, Utility.calculateUtility(game));
        }
        double currentMax = Double.NEGATIVE_INFINITY;
        double currentAlpha = alpha;
        Action chosenAction = new Action();

        if (game.getLegalActions(game.getPlayer(Color.DARK)) != null ) {
            for (Action legalAction : game.getLegalActions(game.getPlayer(Color.DARK))) {
                UtilityAndAction currentTry = minValue(renderNewStatus(game, legalAction), currentAlpha, beta, searchDepthLimit - 1);
                if (currentMax < currentTry.getUtility()) {
                    currentMax = currentTry.getUtility();
                    chosenAction = legalAction;
                }
                if (currentMax >= beta) {
                    return new UtilityAndAction(chosenAction, currentMax);
                }
                currentAlpha = Math.max(currentAlpha, currentMax);
            }
        } else {
            throw new IllegalArgumentException("game already ended");
        }
        return new UtilityAndAction(chosenAction, currentMax);
    }

    private UtilityAndAction minValue(PlayChecker game, double alpha, double beta, int searchDepthLimit) {

        if (game.gameEnded() || searchDepthLimit == 0) {
            return new UtilityAndAction(null, Utility.calculateUtility(game));
        }

        double currentMin = Double.POSITIVE_INFINITY;
        double currentBeta = beta;
        Action chosenAction = new Action();

        if (game.getLegalActions(game.getPlayer(Color.LIGHT)) != null) {
            for (Action legalAction : game.getLegalActions(game.getPlayer(Color.LIGHT))) {
                UtilityAndAction currentTry = maxValue(renderNewStatus(game, legalAction), alpha, currentBeta, searchDepthLimit - 1);
                if (currentTry.getUtility() < currentMin) {
                    currentMin = currentTry.getUtility();
                    chosenAction = legalAction;
                }
                if (currentMin <= alpha) {
                    return new UtilityAndAction(chosenAction, currentMin);
                }
                currentBeta = Math.min(currentBeta, currentMin);
            }
        } else {
            throw new IllegalArgumentException("game already ended");
        }
        return new UtilityAndAction(chosenAction, currentMin);
    }

    public PlayChecker renderNewStatus(PlayChecker game, Action action) {
        PlayChecker newGame = new PlayChecker(game);
        if (action.isJump()) {
            Piece eatenPiece = newGame.getBoard().getPieceByPosition(new Position((action.getPiece().getPosition().getX() + action.getNewPosition().getX()) / 2,
                    (action.getPiece().getPosition().getY() + action.getNewPosition().getY()) / 2));
            eatenPiece.getPlayer().gotEaten(eatenPiece.getSerialNum());
        }
        newGame.getPlayer(action.getPiece().getPlayer().getColor()).updatePiece(action.getPiece().getSerialNum(), action.getNewPosition());
        return newGame;
    }

    private Action getValidInput(Player player) {
        boolean validNum = false;
        boolean validPos = false;
        int serialNum = -1;
        Position newPosition = new Position();
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(player.getBoard().toString());

        while (!validNum || !validPos) {
            if (!validNum) {
                System.out.printf("%s player's move, pick a piece: ", player.getColor());
            } else {
                System.out.printf("%s player's move, assign piece #%d to: ", player.getColor(), serialNum);
            }
            try {
                String inputString = input.readLine();
                if (inputString.trim().equals("*")) {   // Show current board
                    if (validNum) {
                        System.out.printf("%s", getBoard().toString(player.getPieces(serialNum).getPosition()));
                    } else {
                        System.out.print(player.getBoard().toString());
                    }
                } else if (inputString.trim().equals("?")) { // Show available steps
                    if (validNum) {
                        Position[] legalMoves = getLegalMoves(player.getPieces(serialNum));
                        if (legalMoves != null) {
                            System.out.print(player.getBoard().toString(player.getBoard().toString(legalMoves), player.getPieces(serialNum).getPosition()));
                        } else {
                            System.out.print(player.getBoard().toString(player.getPieces(serialNum).getPosition()));
                        }
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (Piece piece : player.getPieces()) {
                            if (piece != null) {
                                stringBuilder.append(piece.getSerialNum()).append(":");
                                Position[] legalMoves = getLegalMoves(piece);
                                if (legalMoves != null) {
                                    for (Position legalMove : legalMoves) {
                                        stringBuilder.append(legalMove.toString()).append(" ");
                                    }
                                    stringBuilder.append(",");
                                }
                            }
                        }
                        System.out.printf("legal moves: \n %s", stringBuilder.toString());
                    }
                }else if (Utility.isInteger(inputString)) {
                    int inputInt = Integer.parseInt(inputString);
                    if (inputInt >= 0 && inputInt < player.getPieces().length && player.getPieces(inputInt) != null) {
                        serialNum = inputInt;
                        validNum = true;
                        System.out.printf("%s", getBoard().toString(player.getPieces(serialNum).getPosition()));

                    } else {

                        System.out.printf("Invalid index number, please pick another number%n");
                    }
                } else if (validNum) {
                    if (inputString.matches("^\\(\\d+,\\d+\\)$") || 
                            inputString.matches("^\\d+,\\d+$")) {
                        if (Utility.isInteger(inputString.replaceAll("\\(", "").replaceAll("\\)", "").split(",")[0]) && Utility.isInteger(inputString.replaceAll("\\(", "").replaceAll("\\)", "").split(",")[1])) {
                            int x = Integer.parseInt(inputString.replaceAll("\\(", "").replaceAll("\\)", "").split(",")[0]);
                            int y = Integer.parseInt(inputString.replaceAll("\\(", "").replaceAll("\\)", "").split(",")[1]);
                            if (isLegalMove(player.getPieces(serialNum), new Position(x, y))) {
                                newPosition = new Position(x, y);
                                validPos = true;
                            } else {
                                System.out.printf("Illegal move: %s\n", inputString);
                            }
                        }
                    } else {
                        System.out.printf("invalid input: %s%n", inputString);
                    }
                } else{
                    System.out.printf("Invalid input: %s%n", inputString);
                }
            } catch (IOException ioe) {
                System.out.printf("OMG it's an IOException(read input): %s%n", ioe.getMessage());
            }
        }
        return new Action(player.getPieces(serialNum), newPosition, isJump(player.getPieces(serialNum), newPosition));
    }

    public boolean gameEnded() {
        return playerLight.getRemainingPieces() == 0 || playerDark.getRemainingPieces() == 0 
            || getNumberOfLegalMoves(playerDark) == 0 || getNumberOfLegalMoves(playerLight) == 0;
    }

    private int getNumberOfLegalMoves(Player player) {
        return getLegalActions(player) == null ? 0 : getLegalActions(player).length;
    }

    private Action[] getLegalActions(Player player) {

        ArrayList<Action> legalActions = new ArrayList<>();
        for (Piece piece : player.getPieces()) {
            if (piece != null) {
                Position[] legalMoves = getLegalMoves(piece);
                if (legalMoves != null) {
                    for (Position position : legalMoves) {
                        legalActions.add(new Action(piece, position, isJump(piece, position)));
                    }
                }
            }
        }
        return legalActions.size() == 0 ? null : legalActions.toArray(new Action[legalActions.size()]);
    }

    private boolean forceJump(Player player) {

        for (Piece piece : player.getPieces()) {
            if (piece != null) {
                if (isPossibleMove(piece, piece.getPositionAfterMove(Move.LEFTJUMP)) ||
                        isPossibleMove(piece, piece.getPositionAfterMove(Move.RIGHTJUMP))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isJump(Piece piece, Position newPosition) {
        return piece.getPositionAfterMove(Move.LEFTJUMP).equals(newPosition) ||
            piece.getPositionAfterMove(Move.RIGHTJUMP).equals(newPosition);
    }

    private Position[] getLegalMoves(Piece piece) {
        boolean forceJump = false;
        ArrayList<Position> legalMoves = new ArrayList<>();
        if (isLegalMove(piece, piece.getPositionAfterMove(Move.LEFTJUMP))) {
            legalMoves.add(piece.getPositionAfterMove(Move.LEFTJUMP));
            forceJump = true;
        }
        if (isLegalMove(piece, piece.getPositionAfterMove(Move.RIGHTJUMP))) {
            legalMoves.add(piece.getPositionAfterMove(Move.RIGHTJUMP));
            forceJump = true;
        }
        if (forceJump) {
            return legalMoves.toArray(new Position[legalMoves.size()]);
        }

        if (isLegalMove(piece, piece.getPositionAfterMove(Move.LEFT))) {
            legalMoves.add(piece.getPositionAfterMove(Move.LEFT));
        }
        if (isLegalMove(piece, piece.getPositionAfterMove(Move.RIGHT))) {
            legalMoves.add(piece.getPositionAfterMove(Move.RIGHT));
        }

        return legalMoves.size() == 0 ? null : legalMoves.toArray(new Position[legalMoves.size()]);
    }

    private boolean isPossibleMove(Piece piece, Position newPosition) {
        boolean possible = false;
        if (board.isLegalPosition(newPosition) && board.isPositionEmpty(newPosition)) {
            if (newPosition.equals(piece.getPositionAfterMove(Move.LEFT)) ||
                    newPosition.equals(piece.getPositionAfterMove(Move.RIGHT))) {
                possible = true;
            } else if (newPosition.equals(piece.getPositionAfterMove(Move.LEFTJUMP)) ||
                    newPosition.equals(piece.getPositionAfterMove(Move.RIGHTJUMP))) {
                Piece eatenPiece = board.getPieceByPosition(new Position((piece.getPosition().getX() + newPosition.getX()) / 2,
                        (piece.getPosition().getY() + newPosition.getY()) / 2));
                if (eatenPiece != null && eatenPiece.getPlayer().getColor() != piece.getPlayer().getColor()) {
                    possible = true;
                }
            }
        }
        return possible;
    }

    private boolean isLegalMove(Piece piece, Position newPosition) {
        boolean legal = false;
        if (isPossibleMove(piece, newPosition)) {
            if (forceJump(piece.getPlayer())) {
                if (isJump(piece, newPosition)) {
                    legal = true;
                }
            } else {
                legal = true;
            }
        }
        return legal;
    }

    public CheckerBoard getBoard() {
        return board;
    }

    public Player getPlayer(Color color) {
        return color == Color.LIGHT ? playerLight : playerDark;
    }
}
