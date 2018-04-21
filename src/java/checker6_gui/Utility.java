package checker6_gui;

/**
 * Author: Yu Jheng Fang
 * Date: Apr 13 2018
 */

public class Utility {

    private final static double WIN_UTILITY_VALUE = 10000d;
    private final static double LOSE_UTILITY_VALUE = -10000d;
    private final static double DRAW_UTILITY_VALUE = 0d;

    /**
         Calculate the score for each piece
         Dark's(AI) piece:
            (+) piece in safe zone
            (+) piece that cannot be eaten
            (+) piece that have other pieces behind

            (-) Light's piece -
     */
    public static double calculateUtility(GameController game) {
        // Note that this designed to calculate the utility value for the AI player
        double utility = 0;
        CheckerBoard board = game.getBoard();
        if (game.gameEnded()) {
            if (game.isVerbose()) {
                System.out.print("Game ended. ");
            }
            switch (game.getResult()) {
                case "DARK":
                    utility = WIN_UTILITY_VALUE + 100 * game.getPlayer(PlayerColor.DARK).getRemainingPieces() - 100 * game.getPlayer(PlayerColor.LIGHT).getRemainingPieces();
                    break;
                case "LIGHT":
                    utility = LOSE_UTILITY_VALUE + 100 * game.getPlayer(PlayerColor.DARK).getRemainingPieces() - 100 * game.getPlayer(PlayerColor.LIGHT).getRemainingPieces();
                    break;
                case "DRAW":
                    utility = DRAW_UTILITY_VALUE;
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognizable end-game status");
            }
        } else {
            if (game.isVerbose()) {
                System.out.print("Search cut off. ");
            }
            int cannotEatBefore = 6;
            int safeAfter = 0;
            for (int i = 0; i < CheckerBoard.getBoardSize(); i ++) {
                for (int j = 0; j < CheckerBoard.getBoardSize(); j ++) {
                    Piece cursor = board.getPieceByPosition(j, i);
                    if (cursor != null) {
                        if (cursor.getPlayer().getColor() == PlayerColor.LIGHT) {
                            if (i < cannotEatBefore) {
                                cannotEatBefore = i;
                            }
                        } else {
                            if (i > safeAfter) {
                                safeAfter = i;
                            }
                        }

                    }
                }
            }
            for (int i = 0; i < CheckerBoard.getBoardSize(); i ++) {
                for (int j = 0; j < CheckerBoard.getBoardSize(); j++) {
                    Piece cursor = board.getPieceByPosition(j, i);
                    if (cursor != null) {
                        if (cursor.getPlayer().getColor() == PlayerColor.LIGHT) {
                            if (i >= safeAfter) {
                                utility -= 25d;
                            } else {
                                if (i == 0 || j == 0 || i == 5 || j == 5) {
                                    utility -= 5d;
                                } else {
                                    utility -= (getPieceBehind(board, cursor) * 3d);
                                }
                            }
                        } else {
                            if (i <= cannotEatBefore) {
                                utility += 40d;
                            } else {
                                if (i == 0 || j == 0 || i == 5 || j == 5) {
                                    utility += 5d;
                                } else {
                                    utility += (getPieceBehind(board, cursor) * 3d);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (game.isVerbose()) {
            System.out.printf("utility: %f%n", utility);
        }
        return utility;
    }

    private static int getPieceBehind(CheckerBoard board, Piece piece) {
        int backDirection = piece.getPlayer().getColor() == PlayerColor.LIGHT ? -1 : 1;
        int pieceBehind = 0;
        Position leftBack = new Position(piece.getPosition().getX() - 1, piece.getPosition().getY() + backDirection);
        Position rightBack = new Position(piece.getPosition().getX() + 1, piece.getPosition().getY() + backDirection);
        pieceBehind += (board.isLegalPosition(leftBack) && !board.isPositionEmpty(leftBack)) ? 1 : 0;
        pieceBehind += (board.isLegalPosition(rightBack) && !board.isPositionEmpty(rightBack)) ? 1 : 0;
        return pieceBehind;
    }

}
