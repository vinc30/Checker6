package checker6;

import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PlayChecker {
    
    public static void main(String[] args) {
        // set up board and players
        PlayChecker game = new PlayChecker();

        // Start playing
        while (!game.gameEnded()) {
            if (game.getNumberOfLegalMoves(game.getPlayer(Color.LIGHT)) > 0) {
                System.out.printf(game.getBoard().toString());
                game.move(game.getPlayer(Color.LIGHT));
            } 
            if (game.getNumberOfLegalMoves(game.getPlayer(Color.DARK)) > 0) {
                System.out.printf(game.getBoard().toString());
                game.move(game.getPlayer(Color.DARK));
            }
        }

        System.out.printf(game.getBoard().toString());
        System.out.printf("Winner: %s\n", game.getResult(game));
        // System.out.printf(board.toString());
    }
    
    private CheckerBoard board;
    private Player playerLight;
    private Player playerDark;
    
    public PlayChecker() {
        board = new CheckerBoard();
        playerLight = new Player(board, Color.LIGHT);
        playerDark = new Player(board, Color.DARK);
        board.putPiece(playerLight.getPieces());
        board.putPiece(playerDark.getPieces());
        
    }
    
    public String getResult(PlayChecker game) {
        if (game.getNumberOfLegalMoves(playerDark) == game.getNumberOfLegalMoves(playerLight)) {
            return "DRAW";
        }
        return game.getNumberOfLegalMoves(playerDark) > game.getNumberOfLegalMoves(playerLight) ?
            "DARK" : "LIGHT";
    }

    public void move(Player player) {
        
        UserInput input = getValidInput(player);
        if (input.getPosition().equals(player.getPieces()[input.getSerialNum()].getPositionAfterMove(Move.LEFTJUMP)) ||
                input.getPosition().equals(player.getPieces()[input.getSerialNum()].getPositionAfterMove(Move.RIGHTJUMP))) {
            Piece eatenPiece = board.getPieceByPosition(new Position((player.getPieces()[input.getSerialNum()].getPosition().getX() + input.getPosition().getX()) / 2,
                    (player.getPieces()[input.getSerialNum()].getPosition().getY() + input.getPosition().getY()) / 2));
            Player rival = getRival(player);
            rival.gotEaten(eatenPiece.getSerialNum());
        }
        player.updatePiece(input.getSerialNum(), input.getPosition());
    }
    
    public UserInput getValidInput(Player player) {
        boolean validNum = false;
        boolean validPos = false;
        int serialNum = -1;
        Position newPosition = new Position();
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        while (!validNum || !validPos) {
            if (!validNum && !validPos) {
                System.out.printf("%s player's move, pick a piece: ", player.getColor());
            } else if (validNum) {
                System.out.printf("%s player's move, assign piece #%d to: ", player.getColor(), serialNum);
            }
            try {
                String inputString = input.readLine();
                if (Utility.isInteger(inputString)) {
                    serialNum = Integer.parseInt(inputString);
                    if (serialNum >= 0 && serialNum < player.getPieces().length && player.getPieces(serialNum) != null) {
                        validNum = true;
                        System.out.printf("%s", getBoard().toString(player.getPieces(serialNum).getPosition(), player));

                    } else {
                        System.out.printf("Invalid index number, please pick another number%n");
                    }
                } else if (validNum == true) {
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
        /*try {
            input.close();
        } catch (IOException ioe) {
            System.out.printf("OMG it's an IOException(close input): %s%n", ioe.getMessage());
        }*/
        return new UserInput(serialNum, newPosition);
    }

    public boolean gameEnded() {
        return playerLight.getRemainingPieces() == 0 || playerDark.getRemainingPieces() == 0 
            || getNumberOfLegalMoves() == 0;
    }

    public int getNumberOfLegalMoves(Player player) {
        int totalLegalMoves = 0;
        for (Piece piece : player.getPieces()) {
            if (piece != null) {
                Position[] legalMoves = getLegalMoves(piece);
                if (legalMoves != null) {
                    totalLegalMoves += legalMoves.length;
                }
            }
        }
        return totalLegalMoves;
    }

    public int getNumberOfLegalMoves() {
        return getNumberOfLegalMoves(playerLight) + getNumberOfLegalMoves(playerDark);
    }
    
    public boolean isJump(Piece piece, Position newPosition) {
        return piece.getPositionAfterMove(Move.LEFTJUMP).equals(newPosition) ||
            piece.getPositionAfterMove(Move.RIGHTJUMP).equals(newPosition);
    }

    public Position[] getLegalMoves(Piece piece) {
        boolean forceJump = false;
        ArrayList<Position> legalMoves = new ArrayList<Position>();
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
    
    public boolean isLegalMove(Piece piece, Position newPosition) {
        boolean legal = false;
        if (board.isLegalPosition(newPosition) && board.isPositionEmpty(newPosition)) {
            if (newPosition.equals(piece.getPositionAfterMove(Move.LEFT)) || 
                    newPosition.equals(piece.getPositionAfterMove(Move.RIGHT)) ||
                        newPosition.equals(piece.getPositionAfterMove(Move.LEFTJUMP)) ||
                            newPosition.equals(piece.getPositionAfterMove(Move.RIGHTJUMP))) {
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

    public Player getRival(Player player) {
        return player.getColor() == Color.LIGHT ? getPlayer(Color.DARK) : getPlayer(Color.LIGHT);
    }
}
