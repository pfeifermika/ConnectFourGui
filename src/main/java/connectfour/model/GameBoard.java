package connectfour.model;

import java.util.*;

public class GameBoard implements Board {


    private Player[][] board = new Player[ROWS][COLS];
    private int level = 4;
    private Player firstPlayer = Player.HUMAN;
    int droppedTokens = 0;
    final static int MAX_DROPPED_TOKENS = ROWS * COLS;

    public GameBoard() {
        Arrays.stream(board).forEach(row -> Arrays.fill(row, Player.TIE));
    }

    private GameBoard(Player[][] board, int level, Player firstPlayer) {
        this.board = board;
        this.level = level;
        this.firstPlayer = firstPlayer;
    }

    /*
    startpunkte berechnen einmal für ein board
     */
    static Coordinates2D[][] startCoordinates;
    static Coordinates2D[] directions;


    private int[] diagUp(){
        int[] groups = new int[4];
        int count = 0;
        int x = 0;
        int y = 0;
        Player player = board[x][y];
        for(int i = 0; i < 4; i++){
            while (board[x][y] != null){
                if (board[x][y] == player) {
                    count++;
                } else {
                    groups[Math.min(CONNECT, count)]++;
                    count = 0;
                    player = board[x][y];
                }
                x++;y++;
            }
        }
        return groups;
    }

    private int[] calculateHorizontal(Player player) {
        int[] groups = new int[4];
        int count = 0;

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (board[i][j] == player) {
                    count++;
                } else {
                    groups[Math.min(CONNECT, count)]++;
                    count = 0;
                }
            }
        }
        return groups;
    }


    /**
     * Checks if a given coordinate is in bounds of the used board.
     * Is 0 indexed.
     *
     * @param coordinate the coordinate to test
     * @return true when the vector is in bounds. false otherwise
     */
    public boolean isInBounds(Coordinates2D coordinate) {
        return coordinate.x() <= ROWS - 1 && coordinate.x() >= 0 &&
                coordinate.y() <= COLS - 1 && coordinate.y() >= 0;
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
        if (isGameOver() || firstPlayer != Player.COMPUTER) {
            throw new IllegalMoveException();
        }
        //TODO
        Random random = new Random();
        int col = random.nextInt(COLS - 1);

        GameBoard newBoard = (GameBoard) clone();
        newBoard.firstPlayer = Player.COMPUTER;
        if (!newBoard.insertChip(col, Player.HUMAN)) {
            newBoard = null;
        }
        return newBoard;
    }

    /**
     * Gets the coordinates of the {@code CONNECT} tiles which are in a line,
     * i.e., a witness of victory. The left lower corner has the smallest
     * coordinates. Should only be called if {@link #getWinner()} returns a
     * value unequal {@code null}. Coordinates are 2-tuples of rows x columns.
     * <p>
     * The result may not be unique!
     *
     * @return The list of coordinates.
     * @throws IllegalStateException There is no winner available.
     */
    public Collection<Coordinates2D> getWitness() {
        return null;
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
        return null; //TODO
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
        if (isGameOver() || firstPlayer != Player.HUMAN) {
            throw new IllegalMoveException("The game is over or its not your turn!");
        } else if (col < 0 || col > COLS - 1) {
            throw new IllegalArgumentException("Argument out of bounds!");
        } else {
            GameBoard newBoard = (GameBoard) clone();
            newBoard.firstPlayer = Player.COMPUTER;
            if (!newBoard.insertChip(col, Player.HUMAN)) {
                newBoard = null;
            }
            return newBoard;
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
        if (col < 1 || col > COLS) {
            return false;
        }

        int i = 0;
        while (board[i][col] == Player.TIE && i < ROWS - 1) {
            i++;
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
        if (level > 1) {
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
        Player[][] clonedBoard = new Player[board.length][];
        for (int i = 0; i < board.length; i++)
            clonedBoard[i] = board[i].clone();
        Player newFirstPlayer = this.getFirstPlayer();
        return new GameBoard(clonedBoard, level, newFirstPlayer);
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

}
