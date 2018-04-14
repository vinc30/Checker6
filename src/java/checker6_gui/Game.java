package checker6_gui;

/**
 * Author: Yu Jheng Fang
 * Date: Apr 13 2018
 */

import javax.swing.*;

/**
 * Main Game
 */
public class Game{
    static JFrame frame;
    private CheckerBoard board;    // chess board
    private GameController game_controller; // game controller
    private GameView game_view;  // game view
    private Player playerLight;
    private Player playerDark;
    /**
     * Constructor: init game, set necessary properties.
     * @param board
     * @param game_controller
     * @param game_view
     */
    public Game(CheckerBoard board, GameController game_controller, GameView game_view, Player playerLight, Player playerDark){
        this.board = board;
        this.game_controller = game_controller;
        this.game_view = game_view;
        this.game_view.bindGameController(game_controller); // bind game controller to game view
        this.playerLight = playerLight;
        this.playerDark = playerDark;
    }


    public void startGame(){
        this.game_view.initWindow(); // init window and begin to draw GUI
    }

    /**
     * Main function
     * @param args
     */
    public static void main(String [] args){
        // initialize Chessboard(model), game view, and game controller
        CheckerBoard board = new CheckerBoard(); // create 6 x 6 chess board.
        Player playerLight = new Player(board, PlayerColor.LIGHT, true);
        Player playerDark = new Player(board, PlayerColor.DARK, false);
        // Place piece on the board
        board.putPiece(playerLight.getPieces());
        board.putPiece(playerDark.getPieces());

        GameView game_view = new GameView(board, 64, playerLight, playerDark); // initialize game view;
        GameController game_controller = new GameController(board, game_view, playerLight, playerDark); // initialize game constroller;

        // init game
        Game game = new Game(board, game_controller, game_view, playerLight, playerDark);

        // start game
        game.startGame();
    }
}