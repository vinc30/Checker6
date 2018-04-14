package checker6_gui;

/**
 * Author: Yu Jheng Fang
 * Date: Apr 13 2018
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * Chessboard View class
 * This class is used to draw chessboard
 *
 */
public class Chessboard_View extends JPanel {
    protected CheckerBoard board; // chessboard that we are using
    protected int tile_size; // size of tile
    protected GameController game_controller; // game controller for the chessboard

    // used to store the coordinate of mouse click
    protected double clicked_x_position = -1;
    protected double clicked_y_position = -1;

    public Chessboard_View(CheckerBoard board, GameController game_controller, int tile_size, int board_width, int board_height){
        this.board = board;   // bind chessboard
        this.game_controller = game_controller;  // bind game controller
        this.tile_size = tile_size;  // set tile size
        this.setPreferredSize(new Dimension(board_width, board_height)); // set preferred size for chessboard view

        /**
         * Mouse press event
         */
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                // save mouse click (x, y) coordinate
                clicked_x_position = e.getPoint().getX();
                clicked_y_position = e.getPoint().getY();

                // System.out.println("X: " + clicked_x_coord + " Y: " + clicked_y_coord);
                repaint(); // this will call this.paint function
            }
        });
    }
    /**
     * Draw images on canvas
     * @param g
     */
    @Override
    public void paint(Graphics g){
        // System.out.println("paint");
        Graphics2D g2d = (Graphics2D) g;
        this.drawBoard(g2d, clicked_x_position, clicked_y_position); // draw empty board

        game_controller.checkUserClick(g2d, clicked_x_position, clicked_y_position);

        if (game_controller.gameEnded()) {
            game_controller.gameOver();
        } else {
            // String message = game_controller.getPlayerForThisTurn().toString() + "'s turn";
            game_controller.updateMessage(game_controller.message);
        }
//        String checkmate_or_stalemate = game_controller.isCheckmateOrStalemate(); // check checkmate or stalemate
//        if(checkmate_or_stalemate == null) { // neither checkmate nor stalemate
//            game_controller.checkUserClick(g2d, clicked_x_coord, clicked_y_coord); // check user mouse click
//        }
//        else if (checkmate_or_stalemate.equals("checkmate")){ // checkmate
//            game_controller.gameIsOver(checkmate_or_stalemate);
//        }
//        else { // stalemate
//            game_controller.gameIsOver(checkmate_or_stalemate);
//        }
    }

    /**
     * Draw tile on board.
     *
     *      Coordinate System
     *      (0, 0) -----------------------> x
     *      |
     *      |
     *      |
     *      |      canvas coordinate system.
     *      |     (x, y)
     *      |
     *      y
     *
     * @param g2d
     * @param x      the x coordinate
     * @param y      the y coordinate
     * @param color  the color to draw
     */
    public void drawTileForBoard(Graphics2D g2d, int x, int y, Color color){
        g2d.setColor(color);       // set color
        g2d.fillRect(x, y, this.tile_size, this.tile_size); // draw tile
    }

    /**
     * Draw Piece on Chessboard
     * @param g2d
     * @param p           the piece we want to draw
     */
    public void drawPiece(Graphics2D g2d, Piece p){
        int piece_x_position = p.getPosition().getX();                // get piece x coordinate (left-bottom coordinate system)
        int piece_y_position = p.getPosition().getY();                // vet piece y coordinate
        int x = piece_x_position * this.tile_size;                    // convert to canvas coordinate system
        int y = (this.board.getHeight() - piece_y_position - 1) * this.tile_size;
        try {
            BufferedImage image = ImageIO.read(new File(p.getPiece_image_path()));  // read image for this piece
            g2d.drawImage(image, x, y, this.tile_size, this.tile_size, null);             // draw the image
        } catch (Exception e) { // this should never happen
            System.out.println("ERROR: Cannot load image file\n"); // this exception should never happen
            System.exit(0);
        }
    }

    /**
     * Draw tiles that the piece can move to
     * @param g2d
     */
    public void drawPossibleMovesForPiece(Graphics2D g2d, ArrayList<Position> positions){
        Piece p;
        Color color;
        int x, y;
        for (Position position : positions) {
            /*
             *  high light possible moves.
             */
            if(this.board.getPieceByPosition(position.getX(), position.getY()) == null) { // that spot is empty
                color = new Color(195, 98, 108);
            }
            else{  // opponent's piece is there
                color = new Color(195, 77, 34);
            }
            x = position.getX() * this.tile_size;  // convert to canvas coordinate
            y = (this.board.getHeight() - 1 - position.getY()) * this.tile_size;
            drawTileForBoard(g2d, x, y, color);

                        /* draw piece at that coordinate */
            p = this.board.getPieceByPosition(position.getX(), position.getY());
            if (p != null)
                drawPiece(g2d, p);
        }
    }
    /**
     * Draw current chessboard
     *
     * @param g2d
     * @param clicked_x_position:  the x coordinate where we clicked
     * @param clicked_y_position:  the y coordinate where we clicked
     */
    public void drawBoard(Graphics2D g2d, double clicked_x_position, double clicked_y_position){
        // draw chess board
        /* #################################
         * ## draw squares on chess board ##
         * #################################
         */
        int count = 0;              // this number is used to judge color for board square
        int i, j;
        Piece p;
        int x, y;                   // x and y coordinate of board square. this coordinate is using top-left system
        Color color;
        for(i = 0; i < this.board.getHeight(); i++){
            for(j = 0; j < this.board.getWidth(); j++){
                x = j * this.tile_size; // calculate canvas coordinate
                y = i * this.tile_size;
                /*
                 * decide the color for square
                 */
                if(count % 2 == 0){
                    color = new Color(255, 206, 158);
                }
                else{
                    color = new Color(209, 139, 71);
                }
                if(clicked_x_position >= x  && clicked_x_position < x + this.tile_size && clicked_y_position >= y && clicked_y_position < y + this.tile_size){
                    color = new Color(140, 91, 49);
                }
                drawTileForBoard(g2d, x, y, color); // draw square
                count++;
            }
            count++;
        }
        /*
         * draw pieces
         */
        for(i = 0; i < this.board.getHeight(); i++){
            for(j = 0; j < this.board.getWidth(); j++){
                /* draw piece */
                p = this.board.getPieceByPosition(j, i);    // get piece at current canvas coordinate (left-top coordinate system)
                if(p != null) {
                    drawPiece(g2d, p);
                }
            }
        }
    }
}
