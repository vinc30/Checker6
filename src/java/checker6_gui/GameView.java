package checker6_gui;

/**
 * Author: Yu Jheng Fang
 * Date: Apr 13 2018
 */

import javax.swing.*;
import java.awt.*;

/**
 *
 * Draw GUI for chess game
 * Game View consists of Chessboard View and Menu View
 */
public class GameView  extends JPanel{
    protected CheckerBoard board; // chessboard that we are using
    protected GameController game_controller; // game controller
    protected Chessboard_View chessboard_view; // chessboard game frame(window)
    protected MenuView menu_view;      // chessboard menu view
    protected int tile_size; // size of tile

    // width and height of the whole game view in pixel
    protected int view_width;
    protected int view_height;

    // width and height of the chessboard in pixel
    protected int board_width;
    protected int board_height;


    // width and height of menu on the right side in pixel
    protected int menu_width;
    protected int menu_height;

    protected Player playerLight;
    protected Player playerDark;

    /**
     * Constructor: init game view
     * @param board  the chessboard that we are using now
     * @param tile_size this size of tile
     */
    public GameView(CheckerBoard board, int tile_size, Player playerLight, Player playerDark){
        // super(new BorderLayout(board.width * tile_size + 500, board.height * tile_size));
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS)); // set layout for game view

        this.board = board;
        this.tile_size = tile_size;

        this.menu_width = 500;   // set menu width
        this.menu_height = this.board.getHeight() * tile_size; // set menu height

        this.board_width = this.board.getWidth() * tile_size; // set board width
        this.board_height = this.board.getHeight() * tile_size; // set board height

        this.view_width = this.board_width  + this.menu_width; // set view width
        this.view_height = this.menu_height; // set view height

        this.chessboard_view = null; // set to null first
        this.setPreferredSize(new Dimension(this.view_width, this.view_height)); // set game view size

        this.playerLight = playerLight;
        this.playerDark = playerDark;
    }

    /**
     * Bing game controller to this game view
     * @param game_controller the game controller that we are going to use.
     */
    public void bindGameController(GameController game_controller){
        this.game_controller = game_controller;
    }


    /**
     * redraw the whole game view
     */
    public void redraw(){
        this.chessboard_view.repaint();
        this.menu_view.repaint();
    }

    /**
     * Initialize game window
     */
    public void initWindow(){
        this.chessboard_view = new Chessboard_View(this.board, this.game_controller, this.tile_size, this.board_width, this.board_height); // initialize chessboard view
        this.add(chessboard_view, BorderLayout.CENTER); // add chessboard view to game view
        this.menu_view = new MenuView(this.menu_width, this.menu_height, this);
        this.add(this.menu_view/*, BorderLayout.LINE_END*/); // add menu view to game view

        getPlayOrder();
        getDifficultyLevel();

        JFrame game_frame = new JFrame("Chess");  // init JFrame object
        game_frame.getContentPane().setPreferredSize(new Dimension(this.view_width, this.view_height));  // set height and width
        game_frame.setResizable(false);    // disable resizable
        game_frame.pack();
        game_frame.setVisible(true);       // set as visible
        game_frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // set close operation
        game_frame.add(this);        // draw canvas
    }

    private void getPlayOrder() {
        boolean gotOrder = false;
        while (!gotOrder) {
            String playOrder = (String)JOptionPane.showInputDialog(null, "Are you playing first? y/n");
            if (playOrder != null && playOrder.length() > 0){
                switch (playOrder){
                    case "y":
                        game_controller.setPlayOrder(1);
                        gotOrder = true;
                        break;
                    case "n":
                        game_controller.setPlayOrder(2);
                        gotOrder = true;
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Invalid input: " + playOrder);
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid input: " + playOrder);
            }
        }

    }

    private void getDifficultyLevel() {
        boolean gotDifficulty = false;
        while (!gotDifficulty) {
            String difficultyLevel = (String)JOptionPane.showInputDialog(null, "What's the difficulty? esay/medium/hard");
            if (difficultyLevel != null && difficultyLevel.length() > 0){
                switch (difficultyLevel){
                    case "easy":
                        game_controller.setDifficulty(3);
                        gotDifficulty = true;
                        break;
                    case "medium":
                        game_controller.setDifficulty(2);
                        gotDifficulty = true;
                        break;
                    case "hard":
                        game_controller.setDifficulty(1);
                        gotDifficulty = true;
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Invalid difficulty: " + difficultyLevel);
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid difficulty: " + difficultyLevel);
            }
        }

    }
}
