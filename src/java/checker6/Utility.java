package checker6;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utility {
    
    private final static int NUMBER_OF_CHAR_PER_LINE = 27;
    private final static double WIN_UTILITY_VALUE = 100d;
    private final static double LOSE_UTILITY_VALUE = -100d;
    private final static double DRAW_UTILITY_VALUE = 0d;
    private static final int SEARCH_DEPTH_LIMIT = 19;
    
    public static boolean isInteger(String s) {
        return isInteger(s, 10);
    }

    public static boolean isInteger(String s, int radix) {
        if (s.isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (Character.digit(s.charAt(i),radix) < 0) {
                return false;
            }
        }
        return true;
    }

    public static int positionToIndex(Position position) {
        return NUMBER_OF_CHAR_PER_LINE * (position.getX() * 2 + 2) + (4 + position.getY() * 4);
    }

    public static int getPlayerOrder() {
        int playerOrder = -1;
        boolean gotOrder = false;
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            while (!gotOrder) {
                String inputString = input.readLine();
                if (Utility.isInteger(inputString)) {
                    playerOrder = Integer.parseInt(inputString);
                    if (playerOrder == 1 || playerOrder == 2) {
                        gotOrder = true;
                    } else {
                        System.out.print("Invalid number: please enter 1 or 2\n");
                    }
                } else {
                    System.out.print("Invalid input: please enter 1 or 2%n");
                }
            }
        } catch (IOException ioe) {
            System.out.printf("OMG it's an IOException(read input): %s%n", ioe.getMessage());
        }
        return playerOrder;
    }

    public static int getDifficulty() {
        int difficulty = -1;
        boolean gotDifficulty = false;
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
            while (!gotDifficulty) {
                String inputString = input.readLine();
                if (Utility.isInteger(inputString)) {
                    difficulty = Integer.parseInt(inputString);
                    if (difficulty == 1 || difficulty == 2 || difficulty == 3) {
                        switch (difficulty) {
                            case 1:
                                difficulty = SEARCH_DEPTH_LIMIT / 3;
                                break;
                            case 2:
                                difficulty = SEARCH_DEPTH_LIMIT / 2;
                                break;
                            case 3:
                                difficulty = SEARCH_DEPTH_LIMIT;
                                break;
                        }
                        gotDifficulty = true;
                    } else {
                        System.out.print("Invalid number: please enter 1, 2 or 3\n");
                    }
                } else {
                    System.out.print("Invalid input: please enter 1, 2 or 3%n");
                }
            }
        } catch (IOException ioe) {
            System.out.printf("OMG it's an IOException(read input): %s%n", ioe.getMessage());
        }
        return difficulty;
    }

    public static double calculateUtility(PlayChecker game) {
        // Note that this designed to calculate the utility value for the AI player
        double utility = 0;
        CheckerBoard board = game.getBoard();
        if (game.gameEnded()) {
            switch (game.getResult()) {
                case "DARK":
                    utility = WIN_UTILITY_VALUE;
                    break;
                case "LIGHT":
                    utility = LOSE_UTILITY_VALUE;
                    break;
                case "DRAW":
                    utility = DRAW_UTILITY_VALUE;
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognizable end-game status");
            }
        } else {
            int cannotEatAfter = 0;
            int safeBefore = 6;
            for (int i = 0; i < CheckerBoard.getBoardSize(); i ++) {
                for (int j = 0; j < CheckerBoard.getBoardSize(); j ++) {
                    if (board.getPieceByPosition(i, j) != null) {
                        if (i == 0 || j == 0 || i == 5 || j == 5) { // pieces in boarder safe-zone
                            utility += (board.getPieceByPosition(i, j).getPlayer().getColor() == Color.DARK) ?
                                    1d : -1d;
                        }
                        if (board.getPieceByPosition(i, j).getPlayer().getColor() == Color.LIGHT) {
                            if (i < safeBefore) {
                                safeBefore = i;
                            } else {
                                if (i > cannotEatAfter) {
                                    cannotEatAfter = i;
                                }
                            }
                        }

                    }
                }
            }
            for (int i = 0; i < CheckerBoard.getBoardSize(); i ++) {
                for (int j = 0; j < CheckerBoard.getBoardSize(); j++) {
                    if (board.getPieceByPosition(i, j) != null) {
                        if (i != 0 && i != 5 && j != 0 && j != 5) {
                            if (board.getPieceByPosition(i, j).getPlayer().getColor() == Color.DARK) {
                                if (i <= safeBefore) {  // pieces in central safe-zone where piece is behind the last LIGHT
                                    utility += 1d;
                                } else if (board.getPieceByPosition(i + 1, j - 1) != null &&
                                        board.getPieceByPosition(i + 1,j - 1).getPlayer().getColor() ==
                                        board.getPieceByPosition(i, j).getPlayer().getColor() &&
                                        board.getPieceByPosition(i + 1, j + 1) != null &&
                                        board.getPieceByPosition(i + 1, j + 1).getPlayer().getColor() ==
                                        board.getPieceByPosition(i, j).getPlayer().getColor()) {
                                    utility += 1d;
                                }
                            } else {
                                if (i >= cannotEatAfter) {
                                    utility -= 1d;
                                }
                            }
                        }
                    }
                }
            }
        }

        // utiliy +=;
        return utility;
        /*
        Calculate the score for each piece
        Dark's(AI) piece:
            piece in safe zone DONE
            piece that cannot be eaten DONE
            piece that have teammate behind within Max(x,y) <= 2 DONE
            piece that have enemy ahead within Max(x,y)
        Light's piece -
         */

        // # Color.DARK's pieces +
        // # Color.LIGHT's piece -
        // # piece in safe zone +
        // # piece to-be eaten -
        // # piece that cannot be eaten +
        // piece that
    }
}
