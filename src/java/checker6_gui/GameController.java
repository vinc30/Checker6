package checker6_gui;

/**
 * Author: Yu Jheng Fang
 * Date: Apr 13 2018
 */

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

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
    private static int DEFAULT_DIFFICULTY = 15;
    private int difficulty;
    private int playOrder = 1;
    private static boolean VERBOSE = true;

    protected String message;    // game message

    /**
     * set VERBOSE be true to see the information during alpha-beta search
     */
    public static boolean isVerbose() {
        return VERBOSE;
    }

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
     * Game ends. That is, when a player has no piece on the board,
     * or both players have no more move to perform.
     */
    public boolean gameEnded() {
        return playerLight.getRemainingPieces() == 0 || playerDark.getRemainingPieces() == 0
                || (getNumberOfLegalMoves(playerDark) == 0 && getNumberOfLegalMoves(playerLight) == 0);
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

    /**
     * The game is over, update menu view and show the winner
     */
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

    /**
     * get number of legal moves for player in this turn
     * @param player
     * @return
     */
    private int getNumberOfLegalMoves(Player player) {
        return getLegalActions(player) == null ? 0 : getLegalActions(player).length;
    }

    /**
     * Get all of the legal actions for player in this turn
     * @param player
     * @return
     */
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
    private ArrayList<Position> showPossibleMovesForPiece(Piece piece){

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

    /**
     * Check if player is forced to make a jump ( or capture move) now
     * @param player
     * @return
     */
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

    /**
     * Check if it's a jump (or capture) move for piece to move to newPosition
     * @param piece
     * @param newPosition
     * @return
     */
    private boolean isJump(Piece piece, Position newPosition) {
        return piece.getPositionAfterMove(Move.LEFTJUMP).equals(newPosition) ||
                piece.getPositionAfterMove(Move.RIGHTJUMP).equals(newPosition);
    }

    /**
     * Get all of the positions of legal move for piece
     * @param piece
     * @return
     */
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

    /**
     * Check if moving piece to newPosition is possible
     * Here, we define a possible move for a piece to be
     * either move of the four basic move for this piece,
     * that is, move diagonal left, move diagonal right,
     * capture diagonal left, capture diagonal right.
     *
     * @param piece
     * @param newPosition
     * @return
     */
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

    /**
     * Check if moving piece to newPosition is legal
     * @param piece
     * @param newPosition
     * @return
     */
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
        if (newPosition != null) {
            if (isPossibleMove(chosen_piece, newPosition)) {
                if (isJump(chosen_piece, newPosition)) {
                    Piece eatenPiece = board.getPieceByPosition(new Position((chosen_piece.getPosition().getX() + newPosition.getX()) / 2,
                            (chosen_piece.getPosition().getY() + newPosition.getY()) / 2));
                    eatenPiece.getPlayer().gotEaten(eatenPiece.getSerialNum());
                    chosen_piece.getPlayer().incrementScore();
                }
                chosen_piece.getPlayer().updatePiece(chosen_piece.getSerialNum(), newPosition);
            }
        }

        this.board.incrementTurns();
        chosen_piece = null;

        updateMessage(getPlayerForThisTurn().toString() + "'s turn");
        this.game_view.chessboard_view.repaint();

        /**
         * Ater human player moved, let AI move
         */
        if (!gameEnded() && this.getPlayerForThisTurn() == playerDark && getNumberOfLegalMoves(playerDark) != 0) {
            // AI's turn for moving
            Thread worker = aiMove(panel);
            worker.start();

            Thread worker2 = aiMoveAgainIfNeeded(panel, worker);
            worker2.start();
        }
        /**
         * If AI does not have any legal move, it forfeit this turn and let human
         * player move again.
         */
        if (!gameEnded() && getPlayerForThisTurn() == playerDark && getNumberOfLegalMoves(playerDark) == 0) {
            movePlayerPieceToEmptyTileIfValid(panel, null);
        }
    }

    /**
     * After AI player moved, if human player has no move to perform
     * The human player forfeit for this round and let AI move again
     * @param panel
     * @param worker
     * @return
     */
    private Thread aiMoveAgainIfNeeded(JPanel panel, Thread worker) {
        Thread aiMoveAgain = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    worker.join();
                } catch (InterruptedException ire) {
                    System.out.print(ire.getMessage());
                }

                if (!gameEnded() && getNumberOfLegalMoves(playerLight) == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ire) {
                        System.out.print(ire.getMessage());
                    }
                    movePlayerPieceToEmptyTileIfValid(panel, null);
                }
            }
        });
        return aiMoveAgain;
    }

    /**
     * AI player search for a move, a perform this move
     * @param panel
     * @return
     */
    private Thread aiMove(JPanel panel) {
        Thread aiThinkAndMove = new Thread(new Runnable() {
            @Override
            public void run() {
                Action aiAction = alphaBetaSearch(GameController.this);
                chosen_piece = aiAction.getPiece();
                movePlayerPieceToEmptyTileIfValid(panel, aiAction.getNewPosition());
            }
        });
        return aiThinkAndMove;
    }

    /**
     * Apply alpha-beta search algorithm for AI player
     * on current game statue
     * @param game
     * @return
     */
    private Action alphaBetaSearch(GameController game) {
        UtilityAndAction result = new UtilityAndAction();
        AtomicReference<Integer> searchCounter = new AtomicReference<>();
        AtomicReference<Integer> pruningCounter = new AtomicReference<>();
        searchCounter.set(0);
        pruningCounter.set(0);

        if (isVerbose()) {
            System.out.print("AI player's move: \n");
        }
        if (difficulty != 0) {
            result = maxValue(game, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, difficulty, searchCounter, pruningCounter);
        } else {
            Random random = new Random();
            Action[] actions = getLegalActions(playerDark);
            result = new UtilityAndAction(actions[random.nextInt(actions.length)], 0);
        }
        if (isVerbose()) {
            if (result.getAction() != null) {
                System.out.printf("Move %s to %s", result.getAction().getPiece().getPosition(), result.getAction().getNewPosition());
            }
            System.out.print("\n");
        }
        return result.getAction();
    }

    /**
     * max function in the MinMax algorithm
     * @param game
     * @param alpha
     * @param beta
     * @param searchDepthLimit
     * @param searchCounter
     * @param pruningCounter
     * @return
     */
    private UtilityAndAction maxValue(GameController game, double alpha, double beta, int searchDepthLimit, AtomicReference<Integer> searchCounter, AtomicReference<Integer> pruningCounter) {

        if (game.gameEnded() || searchDepthLimit == 0 || game.getNumberOfLegalMoves(game.getPlayer(PlayerColor.DARK)) == 0) {
            if (isVerbose()) {
                System.out.printf("depth: %d, pruned: %d, nodes: #%d ", difficulty - searchDepthLimit, pruningCounter.get(), searchCounter.get());
            }
            return new UtilityAndAction(null, Utility.calculateUtility(game));
        }
        double currentMax = Double.NEGATIVE_INFINITY;
        double currentAlpha = alpha;
        Action chosenAction = new Action();

        if (game.getLegalActions(game.getPlayer(PlayerColor.DARK)) != null ) {
            for (Action legalAction : game.getLegalActions(game.getPlayer(PlayerColor.DARK))) {
                searchCounter.set(searchCounter.get() + 1);
                UtilityAndAction currentTry = minValue(renderNewStatus(game, legalAction), currentAlpha, beta, searchDepthLimit - 1, searchCounter, pruningCounter);
                if (currentMax < currentTry.getUtility()) {
                    currentMax = currentTry.getUtility();
                    chosenAction = legalAction;
                }
                if (currentMax >= beta) {
                    pruningCounter.set(pruningCounter.get() + 1);
//                    if (isVerbose()) {
//                        System.out.printf("depth: %d, node#%d Pruning: max at %f (alpha: %f, beta: %f)\n",difficulty - searchDepthLimit, searchCounter.get(), currentMax, currentAlpha, beta);
//                    }
                    return new UtilityAndAction(chosenAction, currentMax);
                }
                currentAlpha = Math.max(currentAlpha, currentMax);
            }
        } else {
            throw new IllegalArgumentException("game already ended");
        }
        return new UtilityAndAction(chosenAction, currentMax);
    }

    /**
     * min function in the MinMax algorithm
     * @param game
     * @param alpha
     * @param beta
     * @param searchDepthLimit
     * @param searchCounter
     * @param pruningCounter
     * @return
     */
    private UtilityAndAction minValue(GameController game, double alpha, double beta, int searchDepthLimit, AtomicReference<Integer> searchCounter, AtomicReference<Integer> pruningCounter) {

        if (game.gameEnded() || searchDepthLimit == 0 || game.getNumberOfLegalMoves(game.getPlayer(PlayerColor.LIGHT)) == 0) {
            if (isVerbose()) {
                System.out.printf("depth: %d, pruned: %d, nodes: #%d ", difficulty - searchDepthLimit, pruningCounter.get(), searchCounter.get());
            }
            return new UtilityAndAction(null, Utility.calculateUtility(game));
        }

        double currentMin = Double.POSITIVE_INFINITY;
        double currentBeta = beta;
        Action chosenAction = new Action();

        if (game.getLegalActions(game.getPlayer(PlayerColor.LIGHT)) != null) {
            for (Action legalAction : game.getLegalActions(game.getPlayer(PlayerColor.LIGHT))) {
                searchCounter.set(searchCounter.get() + 1);
                UtilityAndAction currentTry = maxValue(renderNewStatus(game, legalAction), alpha, currentBeta, searchDepthLimit - 1, searchCounter, pruningCounter);
                if (currentTry.getUtility() < currentMin) {
                    currentMin = currentTry.getUtility();
                    chosenAction = legalAction;
                }
                if (currentMin <= alpha) {
//                    if (isVerbose()) {
//                        System.out.printf("depth: %d, node#%d Pruning: min at %f (alpha: %f, beta: %f)\n", difficulty - searchDepthLimit, searchCounter.get(), currentMin, alpha, currentBeta);
//                    }
                    pruningCounter.set(pruningCounter.get() + 1);
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
                    this.game_view.chessboard_view.drawPossibleMovesForPiece(g2d, this.showPossibleMovesForPiece(p)); // draw possible moves
                } else { // player clicked opponent's piece
                    this.chosen_piece = null;
                }
            } else {
                if (this.chosen_piece != null) {
                    if (chosen_piece.getPlayer() == getPlayerForThisTurn() && isLegalMove(chosen_piece, new Position(x, y))) {
                        // that means  p == null, and player clicked a tile that is not occupied
                        this.movePlayerPieceToEmptyTileIfValid(this.game_view, newPosition);
                    } else {
                        this.game_view.chessboard_view.drawPossibleMovesForPiece(g2d, this.showPossibleMovesForPiece(chosen_piece)); // draw possible moves
                    }
                }
            }
        }
    }

    /**
     * Update game message on menu view
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
        updateMessage("Have fun!\n" + (playOrder == 1 ? playerLight.getName() : playerDark.getName()) + "'s turn");

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

    /**
     * set the level of difficulty
     * @param n
     */
    public void setDifficulty(int n) {
        this.difficulty = n * DEFAULT_DIFFICULTY / 3;
    }

    /**
     * Get the player class of the color
     * @param color
     * @return
     */
    public Player getPlayer(PlayerColor color) {
        return color == PlayerColor.DARK ? playerDark : playerLight;
    }

    private boolean gameStarted() {
        return game_start;
    }

}
