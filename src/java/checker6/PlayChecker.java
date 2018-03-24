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
                System.out.printf("Move piece to position: ");
                game.move(game.getPlayer(Color.LIGHT));
            } 
            if (game.getNumberOfLegalMoves(game.getPlayer(Color.DARK)) > 0) {
                System.out.printf(game.getBoard().toString());
                System.out.printf("Move piece to position: ");
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
        int pieceNum = getValidPieceNum(player);
        // check targetPosition valid
        Position newPosition = getValidPosition(player, pieceNum);
        player.updatePiece(pieceNum, newPosition);
    }
    
    public Position getValidPosition(Player player, int pieceNum) {
        
        // Scanner scan = new Scanner(System.in);
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        boolean validPos = false;
        Position position = null;
        while (!validPos) {
            try {
                int x = Integer.parseInt(input.readLine());
                int y = Integer.parseInt(input.readLine());
                if (isLegalMove(player.getPieces(pieceNum), new Position(x, y))) {
                    position = new Position(x, y);
                    validPos = true;
                }
            } catch (IOException ioe) {
                System.out.printf(ioe.getMessage());
            }
        }
        // scan.close();
        return position;
    }

    public int getValidPieceNum(Player player) {
        
        // Scanner scan = new Scanner(System.in);
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        boolean validNum = false;
        int serialNum = -1;
        while (!validNum) {
            try {
                serialNum = Integer.parseInt(input.readLine());
                if (serialNum > 0 && serialNum < player.getPieces().length && player.getPieces(serialNum) != null) {
                    validNum = true;
                }
            } catch (IOException ioe) {
                System.out.printf(ioe.getMessage());
            }
        }
        // scan.close();
        return serialNum;
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
}
