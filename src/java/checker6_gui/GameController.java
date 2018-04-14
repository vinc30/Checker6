package checker6_gui;

/**
 * Author: Yu Jheng Fang
 * Date: Apr 13 2018
 */

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Game Controller
 */
public class GameController {
    protected CheckerBoard board;  // chess board
    protected GameView game_view; // game view
    protected Piece chosen_piece;  // the piece that is chosen by player
    protected boolean game_start; // game already starts?

    protected Player playerLight;
    protected Player playerDark;
    private static int DEFAULT_DIFFICULTY = 18;
    private int difficulty;
    private int playOrder = 1;

    protected String message;    // game message

    /**
     * Constructor: initialize game controller
     * @param board
     * @param game_view
     */
    public GameController(CheckerBoard board, GameView game_view, Player playerLight, Player playerDark){
        this.board = board;      // bind chessboard to current game controller
        this.game_view = game_view; // bind game view to current game controller
        this.chosen_piece = null; // no piece is chosen by player yet
        this.game_start = false;  // game is not started yet, need to click start button.
        this.playerLight = playerLight;
        this.playerDark = playerDark;
        this.message = "Press Start button to start the game"; // game message
        this.difficulty = DEFAULT_DIFFICULTY;
    }

    public GameController(GameController game) {
        this.board = new CheckerBoard();
        this.playerLight = new Player(board, game.getPlayer(PlayerColor.LIGHT));
        this.playerDark = new Player(board, game.getPlayer(PlayerColor.DARK));
        board.putPiece(playerLight.getPieces());
        board.putPiece(playerDark.getPieces());
    }

    /**
     * Game ends
     */
    public boolean gameEnded() {
        return playerLight.getRemainingPieces() == 0 || playerDark.getRemainingPieces() == 0
                || getNumberOfLegalMoves(playerDark) == 0 || getNumberOfLegalMoves(playerLight) == 0;
    }
    public String getResult() {
        if (playerDark.getRemainingPieces() == playerLight.getRemainingPieces()) {
            return "DRAW";
        }
        return playerDark.getRemainingPieces() > playerLight.getRemainingPieces() ?
                "DARK" : "LIGHT";
    }

    public void setPlayOrder(int order) {
        this.playOrder = order;
    }

    public void gameOver(){
        if(!gameStarted()) // game already over
            return;
        // this.game_start = false; // game not started now.
        if (getResult() == "DRAW") {
            updateMessage(getResult());
        } else {
            updateMessage((getResult() == "LIGHT" ? playerLight.getName() : playerDark.getName()) + " wins!");
        }

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

    /**
     * Return possible move coordinates for piece; eliminate suicide move
     *
     * @param piece the piece we want to move.
     * @return coordinate lists
     */
    private ArrayList<Position> showPossibelMovesForPiece(Piece piece){

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
            return legalMoves;
        }

        if (isLegalMove(piece, piece.getPositionAfterMove(Move.LEFT))) {
            legalMoves.add(piece.getPositionAfterMove(Move.LEFT));
        }
        if (isLegalMove(piece, piece.getPositionAfterMove(Move.RIGHT))) {
            legalMoves.add(piece.getPositionAfterMove(Move.RIGHT));
        }

//        return legalMoves.size() == 0 ? null : legalMoves.toArray(new Position[legalMoves.size()]);
        return legalMoves;
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



    /**
     * Move player's piece to unoccupied tile if valid
     * @param panel
     * @param newPosition     the new position to move to
     */
    private void movePlayerPieceToEmptyTileIfValid(JPanel panel, Position newPosition){
        if (isPossibleMove(chosen_piece, newPosition)) {
            if (isJump(chosen_piece, newPosition)) {
                Piece eatenPiece = board.getPieceByPosition(new Position((chosen_piece.getPosition().getX() + newPosition.getX()) / 2,
                        (chosen_piece.getPosition().getY() + newPosition.getY()) / 2));
                eatenPiece.getPlayer().gotEaten(eatenPiece.getSerialNum());
                chosen_piece.getPlayer().incrementScore();
            }
            chosen_piece.getPlayer().updatePiece(chosen_piece.getSerialNum(), newPosition);
        }

        this.board.incrementTurns();
        panel.repaint();
        updateMessage(getPlayerForThisTurn().toString() + "'s turn");
        if (!gameEnded() && this.getPlayerForThisTurn() == playerDark) {
            // AI's turn for moving
            aiMove(panel);
        }
    }

    private void aiMove(JPanel panel) {
        Thread aiThinking = new Thread(new Runnable() {
            @Override
            public void run() {
                Action aiAction = alphaBetaSearch(GameController.this);
                chosen_piece = aiAction.getPiece();
                movePlayerPieceToEmptyTileIfValid(panel, aiAction.getNewPosition());
            }
        });
        aiThinking.start();
    }

    private Action alphaBetaSearch(GameController game) {
        UtilityAndAction result = new UtilityAndAction();

        System.out.print("AI player's move: %n");
        result = maxValue(game, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, difficulty);
        if (result.getAction() != null) {
            System.out.printf("Move %s to %s", result.getAction().getPiece().getPosition(), result.getAction().getNewPosition());
        }
        System.out.print("\n");
        return result.getAction();
    }

    private UtilityAndAction maxValue(GameController game, double alpha, double beta, int searchDepthLimit) {

        if (game.gameEnded() || searchDepthLimit == 0) {
            return new UtilityAndAction(null, Utility.calculateUtility(game));
        }
        double currentMax = Double.NEGATIVE_INFINITY;
        double currentAlpha = alpha;
        Action chosenAction = new Action();

        if (game.getLegalActions(game.getPlayer(PlayerColor.DARK)) != null ) {
            for (Action legalAction : game.getLegalActions(game.getPlayer(PlayerColor.DARK))) {
                UtilityAndAction currentTry = minValue(renderNewStatus(game, legalAction), currentAlpha, beta, searchDepthLimit - 1);
                if (currentMax < currentTry.getUtility()) {
                    currentMax = currentTry.getUtility();
                    chosenAction = legalAction;
                }
                if (currentMax >= beta) {
                    System.out.printf("Pruning: max at %f (alpha: %f, beta: %f)\n", currentMax, currentAlpha, beta);
                    return new UtilityAndAction(chosenAction, currentMax);
                }
                currentAlpha = Math.max(currentAlpha, currentMax);
            }
        } else {
            throw new IllegalArgumentException("game already ended");
        }
        return new UtilityAndAction(chosenAction, currentMax);
    }

    private UtilityAndAction minValue(GameController game, double alpha, double beta, int searchDepthLimit) {

        if (game.gameEnded() || searchDepthLimit == 0) {
            return new UtilityAndAction(null, Utility.calculateUtility(game));
        }

        double currentMin = Double.POSITIVE_INFINITY;
        double currentBeta = beta;
        Action chosenAction = new Action();

        if (game.getLegalActions(game.getPlayer(PlayerColor.LIGHT)) != null) {
            for (Action legalAction : game.getLegalActions(game.getPlayer(PlayerColor.LIGHT))) {
                UtilityAndAction currentTry = maxValue(renderNewStatus(game, legalAction), alpha, currentBeta, searchDepthLimit - 1);
                if (currentTry.getUtility() < currentMin) {
                    currentMin = currentTry.getUtility();
                    chosenAction = legalAction;
                }
                if (currentMin <= alpha) {
                    System.out.printf("Pruning: min at %f (alpha: %f, beta: %f)\n", currentMin, alpha, currentBeta);
                    return new UtilityAndAction(chosenAction, currentMin);
                }
                currentBeta = Math.min(currentBeta, currentMin);
            }
        } else {
            throw new IllegalArgumentException("game already ended");
        }
        return new UtilityAndAction(chosenAction, currentMin);
    }

    public GameController renderNewStatus(GameController game, Action action) {
        GameController newGame = new GameController(game);
        if (action.isJump()) {
            Piece eatenPiece = newGame.getBoard().getPieceByPosition(new Position((action.getPiece().getPosition().getX() + action.getNewPosition().getX()) / 2,
                    (action.getPiece().getPosition().getY() + action.getNewPosition().getY()) / 2));
            eatenPiece.getPlayer().gotEaten(eatenPiece.getSerialNum());
        }
        newGame.getPlayer(action.getPiece().getPlayer().getColor()).updatePiece(action.getPiece().getSerialNum(), action.getNewPosition());
        return newGame;
    }


    /**
     * Check user mouse click, and update gui.
     * @param g2d
     * @param clicked_x_position
     * @param clicked_y_position
     */
    public void checkUserClick(Graphics2D g2d, double clicked_x_position, double clicked_y_position){
        if (getPlayerForThisTurn() != playerLight) {
            return;
        }
        if(!gameStarted()){ // game is not started yet. so we don't need to check user mouse click.
            return;
        }
        int x, y;
        Piece p;
        /*
         * check mouse click.
         */
        if(clicked_x_position >= 0 && clicked_y_position >= 0) {     // valid click scope
            x = (int) (clicked_x_position / 64);                  // convert to left-bottom chess board coordinate system
            y = this.board.getHeight() - 1 - (int) (clicked_y_position / 64);
            p = this.board.getPieceByPosition(x, y);
            Position newPosition = new Position(x, y);

            /*
             *  Now we clicked a spot/piece
             *
             */
            if (p != null) { // player clicked a piece; show its possible moves
                if(p.getPlayer() == getPlayerForThisTurn()) { // player clicked his/her own piece
                    this.chosen_piece = p;       // save as chosen_piece
                    this.game_view.chessboard_view.drawPossibleMovesForPiece(g2d, this.showPossibelMovesForPiece(p)); // draw possible moves
                } else { // player clicked opponent's piece
                    this.chosen_piece = null;
                }
            } else {
                if (this.chosen_piece != null) {
                    if (chosen_piece.getPlayer() == getPlayerForThisTurn() && isLegalMove(chosen_piece, new Position(x, y))) {
                        // that means  p == null, and player clicked a tile that is not occupied
                        this.movePlayerPieceToEmptyTileIfValid(this.game_view, newPosition);
                    } else {
                        this.game_view.chessboard_view.drawPossibleMovesForPiece(g2d, this.showPossibelMovesForPiece(chosen_piece)); // draw possible moves
                    }
                }
            }
        }
    }

    /**
     * Update game message
     * @param message
     */
    public void updateMessage(String message){
        // set message
        this.message = message;

        // redraw menu
        this.game_view.menu_view.drawMenu(this.message);
    }

    /**
     *
     * Start a new game
     */
//
    private void startNewGame(CheckerBoard newBoard){
        // this.board = newBoard;
        this.game_view.chessboard_view.clicked_x_position = -1;  // reset click x position
        this.game_view.chessboard_view.clicked_y_position = -1;  // reset click y position

        // rebind the chessboard to GameView, GameController
        this.board = newBoard;
        this.game_view.board = newBoard;
        this.game_view.chessboard_view.board = newBoard;
//        this.board.generateStandardBoard();
        this.game_view.redraw();


        // this.chessboard_history_log = new Stack<Chessboard_Log>();   // init chessboard history log
        this.game_start = true; // start game
        updateMessage("Have fun in game!!\n" + (playOrder == 1 ? playerLight.getName() : playerDark.getName()) + "'s turn");

        if (playOrder == 2) {
            this.board.incrementTurns();
            aiMove(GameController.this.game_view);
        }
    }

    /**
     * Player clicked start button
     */
    public void clickedStartButton(){
        if(gameStarted()){ // game already started, so this func should do nothing
            JOptionPane.showMessageDialog(null, "Game already started");
            return;
        }
        this.startNewGame(this.board);
    }

    /**
     * Player clicked Difficulty button
     */
    public void clickedDifficultyButton(JButton btn){
        if(gameStarted()){
            JOptionPane.showMessageDialog(null, "You cannot change your name during the game");
            return;
        }
        String difficultyLevel = (String)JOptionPane.showInputDialog(null, "What's the difficulty? esay/medium/hard");
        if (difficultyLevel != null && difficultyLevel.length() > 0){
            switch (difficultyLevel){
                case "easy":
                    this.difficulty /= 3;
                    break;
                case "medium":
                    this.difficulty /= 2;
                    break;
                case "hard":
                    this.difficulty /= 1;
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid difficulty: " + difficultyLevel);
                    break;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Invalid difficulty: " + difficultyLevel);
        }

    }

    /**
     * Update Player Name
     * @param btn
     */
    public void updatePlayerName(JButton btn){
        if(gameStarted()){
            JOptionPane.showMessageDialog(null, "You cannot change name during the game");
            return;
        }
        String newName = (String)JOptionPane.showInputDialog(null, "Please new name for " + btn.getText());
        if (newName != null && newName.length() > 0){
            if(btn.getText().equals(playerLight.getName())){ // update playerLight name
                if(playerDark.getName().equals(newName)){ // playerLight and playerDark cannot have the same name
                    JOptionPane.showMessageDialog(null, "Invalid name: " + newName);
                    return;
                }
                playerLight.setName(newName);
            }
            else{ // update playerDark name
                if(playerLight.getName().equals(newName)){ // playerLight and playerDark cannot have the same name
                    JOptionPane.showMessageDialog(null, "Invalid name: " + newName);
                    return;
                }
                playerDark.setName(newName);
            }
            btn.setText(newName); // update player name
        }

        updateMessage((this.getPlayerForThisTurn().getColor() == PlayerColor.LIGHT ? playerLight.getName() : playerDark.getName()) + "'s turn");

        // redraw everything
        this.game_view.redraw();
    }

    /**
     * Get the player for this turn
     * @return the player for this turn
     */
    public Player getPlayerForThisTurn(){
        return board.getTurns() % 2 == 0 ?  playerLight : playerDark;
    }

    public CheckerBoard getBoard() {
        return board;
    }

    public void setDifficulty(int n) {
        this.difficulty /= n;
    }

    public Player getPlayer(PlayerColor color) {
        return color == PlayerColor.DARK ? playerDark : playerLight;
    }

    private boolean gameStarted() {
        return game_start;
    }


}
