package connectfour.model;

import connectfour.model.ai.Minimax;
import connectfour.model.exceptions.IllegalMoveException;
import connectfour.model.utility.BoardUtility;

import java.util.*;

/**
 * Models a state of a game of ConnectFour.
 *
 * For programming ease the top left corner is coordinate (0,0).
 */
public class GameState implements Board {

    private Player[][] board = new Player[ROWS][COLS];
    private int level = 4;
    private Player firstPlayer;
    private Set<Coordinates2D> witness;


    private final int[] humanGroups = new int[3];
    private final int[] computerGroups = new int[3];

    private final static Coordinates2D[] START_COORDINATES
            = BoardUtility.calculateStartCoordinates();

    private static final Coordinates2D[] DIRECTION_VECTORS = {
            new Coordinates2D(1, 0),  //vertical
            new Coordinates2D(0, 1),  //horizontal
            new Coordinates2D(-1, 1), //diag up
            new Coordinates2D(1, 1)   //diag down
    };

    public GameState() {
        Arrays.stream(board).forEach(row -> Arrays.fill(row, Player.TIE));
    }



    /**
     * Checks if a given coordinate is in bounds of the used board.
     * Is 0 indexed.
     *
     * @param row the row to check
     * @param col the col to check
     * @return true when the coordinates are in bounds, false otherwise.
     */
    private boolean isInBounds(int row, int col) {
        return row <= ROWS - 1 && row >= 0 &&
                col <= COLS - 1 && col >= 0;
    }


    /**
     * Executes a machine move. This method does not change the state of this
     * instance, which is treated here as immutable. Instead, a new board/game
     * is returned, which is a copy of {@code this} with the move executed.
     *
     * @return A new board with the move executed.
     * @throws IllegalMoveException The game is already over, or it is not the
     *                              the machine's turn.
     */
    public Board machineMove() {
        if (firstPlayer == null) {
            firstPlayer = Player.COMPUTER;
        }
        if (isGameOver() || firstPlayer != Player.COMPUTER) {
            throw new IllegalMoveException();
        }

        Minimax minimax = new Minimax();

        int col = minimax.calculateMove(this,this.level);
        System.out.println(col);
        GameState newBoard = (GameState) clone();
        newBoard.firstPlayer = Player.HUMAN;
        if (!newBoard.insertChip(col, Player.COMPUTER)) {
            newBoard = null;
        }
        return newBoard;
    }

    /**
     * {@inheritDoc}
     *
     * @param col The column where to put the tile of the human.
     * @return A new board with the move executed. If the move is not valid,
     * i.e. {@code col} was full before, then {@code null} will be
     * returned.
     * @throws IllegalMoveException     The game is already over, or it is not the
     *                                  the human's turn.
     * @throws IllegalArgumentException The provided column {@code col} is
     *                                  invalid, i.e. not found on the grid.
     */
    public Board move(int col) {
        if (firstPlayer == null) {
            firstPlayer = Player.HUMAN;
        }

        if (isGameOver() || firstPlayer != Player.HUMAN) {
            throw new IllegalMoveException("The game is over or its not your turn!");
        } else if (col < 0 || col > COLS - 1) {
            throw new IllegalArgumentException("Argument out of bounds!");
        } else {
            GameState newBoard = (GameState) clone();
            newBoard.firstPlayer = Player.COMPUTER;
            if (!newBoard.insertChip(col, Player.HUMAN)) {
                newBoard = null;
            }
            return newBoard;
        }
    }

    /**
     * Gets the coordinates of the {@code CONNECT} tiles which are in a line,
     * i.e., a witness of victory. The left lower corner has the smallest
     * coordinates. Should only be called if {@link #getWinner()} returns a
     * value unequal {@code null}. Coordinates are 2-tuples of rows row columns.
     * <p>
     * The result may not be unique!
     *
     * @return The list of coordinates.
     * @throws IllegalStateException There is no winner available.
     */
    public Collection<Coordinates2D> getWitness() {
       // Player player = getWinner();
       // if(player == null){
       //     throw new IllegalStateException("There is no winner available");
       // }k
        return witness;
    }

    /**
     * Gets the player who should start or already has started the game.
     *
     * @return The player who makes the initial move.
     */
    public Player getFirstPlayer() {
        return firstPlayer;
    }

    /**
     * Checks if game is over. Either one player has won or there is a tie and
     * all slots are filled with tiles.
     *
     * @return {@code true} if and only if the game is over.
     */
    public boolean isGameOver() {
        return isAllSlotsFilled() || getWinner() != null;
    }

    private boolean isAllSlotsFilled() {
        for (int i = 0; i < board[0].length; i++) {
            if (board[0][i] == Player.TIE) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @return The winner or {@code null} in case of a tie or if the game is
     * not finished yet.
     */
    public Player getWinner() {
        countGroups();

        if (humanGroups[2] != 0) {
            return Player.HUMAN;
        } else if (computerGroups[2] != 0) {
            return Player.COMPUTER;
        } else {
            return null;
        }


    }


    /**
     * Inserts a chip of the given player into the given slot.
     * Calculates the index so first column would be col = 0.
     *
     * @param col    the column, the chip gets inserted into starting from 0.
     * @param player the player whose chip gets inserted
     * @return true if the operation was successful, false otherwise
     */
    public boolean insertChip(int col, Player player) {
        if (col < 0 || col > COLS) {
            return false;
        }

        int i = ROWS - 1;
        while (board[i][col] != Player.TIE && i > 0) {
            i--;
        }

        if (board[i][col] == Player.TIE) {
            board[i][col] = player;
            return true;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setLevel(int level) {
        if (level > 0) {
            this.level = level;
        }
    }


    /**
     * {@inheritDoc}
     *
     * @param row The row of the slot in the game grid, starting from 0.
     * @param col The column of the slot in the game grid starting from 0.
     * @return The slot's content.
     */
    public Player getSlot(int row, int col) {
        return board[row][col];
    }

    /**
     * {@inheritDoc}
     *
     * @return A clone.
     */
    public Board clone() {

        Player[][] clonedArray = new Player[board.length][];
        for (int i = 0; i < board.length; i++)
            clonedArray[i] = board[i].clone();

        GameState clonedBoard = new GameState();
        clonedBoard.board = clonedArray;
        clonedBoard.firstPlayer = this.getFirstPlayer();
        clonedBoard.level = this.level;

        return clonedBoard;
    }

    /**
     * {@inheritDoc}
     *
     * @return The string representation of the current Connect Four game.
     */
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < board.length; i++) {
            for (int k = 0; k < board[0].length; k++) {
                strBuilder.append(board[i][k].toString()).append(" ");
            }
            strBuilder.append("\n");
        }
        return strBuilder.toString();
    }

    protected int evaluateBoard() {
        countGroups();

        int p = 50 + computerGroups[0] + 4 * computerGroups[1]
                + 5000 * computerGroups[2] - humanGroups[0]
                - 4 * humanGroups[1] - 500000 * humanGroups[2];

        int q = getTokensInCol(1, Player.COMPUTER)
                + 2 * getTokensInCol(2, Player.COMPUTER)
                + 3 * getTokensInCol(3, Player.COMPUTER)
                + 2 * getTokensInCol(4, Player.COMPUTER)
                + getTokensInCol(5, Player.COMPUTER)
                - getTokensInCol(1, Player.HUMAN)
                - 2 * getTokensInCol(2, Player.HUMAN)
                - 3 * getTokensInCol(3, Player.HUMAN)
                - 2 * getTokensInCol(4, Player.HUMAN)
                - getTokensInCol(5, Player.HUMAN);

        int r = 0;
        if (getWinner() == Player.COMPUTER) {
            r = 5000000;
        }

        return p + q + r;
    }

    private int getTokensInCol(int col, Player player) {
        int count = 0;
        for (Player[] players : board) {
            if (players[col] == player) {
                count++;
            }
        }
        return count;
    }

    /**
     * Loops over every entry in {@code START_COORDINATES} and calculates the
     * groups for every direction for that coordinate.
     */
    private void countGroups() {
        int diagCount = ROWS + COLS - 1;
        for (int i = 0; i < START_COORDINATES.length; i++) {
            if (i <= diagCount) {
                //from left down corner to right up corner
                calcGroupsInRow(START_COORDINATES[i], DIRECTION_VECTORS[2]);
            }
            if (i >= COLS - 1 && i <= diagCount) {
                //horizontal
                calcGroupsInRow(START_COORDINATES[i], DIRECTION_VECTORS[1]);
            }
            if (i >= COLS - 1) {
                //from left upper corner to right down corner
                calcGroupsInRow(START_COORDINATES[i], DIRECTION_VECTORS[3]);
            }
            if (i >= diagCount - 1) {
                //vertical
                calcGroupsInRow(START_COORDINATES[i], DIRECTION_VECTORS[0]);
            }
        }
    }

    private void calcGroupsInRow(Coordinates2D start, Coordinates2D incr) {
        int count = 0;
        int row = start.row();
        int col = start.col();
        Player player = board[row][col];

        //loop over every coordinate in one line/diag
        while (isInBounds(row, col)) {
            if (player == Player.TIE) {
                count = 1;
            } else if (board[row][col] == player) {
                count++;
            } else {
                increaseGroupCount(count, player,row,col);
                count = 1;
            }
            player = board[row][col];
            row += incr.row();
            col += incr.col();
        }
        increaseGroupCount(count, player,row,col);
    }

    private void increaseGroupCount(int count, Player player,int row, int col) {
        if (count == 0 || count == 1) {
            return;
        }
        if(count > 3){
            this.witness = floodFill(row,col,player);
        }
        int index = Math.min(CONNECT - 2, count - 2);

        if (player == Player.HUMAN) {
            humanGroups[index]++;
        } else if (player == Player.COMPUTER) {
            computerGroups[index]++;
        }
    }



    //TODO
    private Set<Coordinates2D> floodFill(int row, int col, Player player){
        if(!isInBounds(row,col) || board[row][col] != player){
            return Collections.emptySet();
        } else {

            Set<Coordinates2D> cordSet = new LinkedHashSet<>();
            cordSet.addAll(floodFill(row + 1, col, player));
            cordSet.addAll(floodFill(row - 1, col, player));
            cordSet.addAll(floodFill(row, col + 1, player));
            cordSet.addAll(floodFill(row, col - 1, player));

           return cordSet;
        }

    }

    protected void setFirstPlayer(Player player){
        this.firstPlayer = player;
    }
}
