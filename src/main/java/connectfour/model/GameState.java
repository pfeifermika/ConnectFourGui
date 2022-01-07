package connectfour.model;

import connectfour.model.exceptions.IllegalMoveException;
import connectfour.model.utility.ConnectFourUtility;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;


/**
 * Represents a state of a game of ConnectFour.
 * <p>
 * An instance of this Class is treated as immutable, therefore a new instance
 * with the move executed is returned, when calling {@link GameState#move(int)}
 * or {@link GameState#machineMove()}.
 * <p>
 * This class implements public methods to execute a human or machine move,
 * to change the difficulty level, to get the player to move, to check if the
 * game is over, to check if there is a winner, to get the witnesses of a win
 * and the player token of a specific slot.
 * <p>
 * The top left corner of {@code board} is the coordinate (0,0).
 */
public class GameState implements Board {

    /**
     * The maximum difficulty setting for the machine.
     */
    private static final int MAX_LEVEL = 5;

    /**
     * Contains the starting coordinates used to calculate the groups.
     */
    private static final Coordinates2D[] START_COORDINATES
            = ConnectFourUtility.calculateAllStartCoordinates();

    /**
     * Direction vectors representing different
     * directions to traverse the board when calculating groups.
     */
    private static final Coordinates2D[] DIRECTION_VECTORS = {
            new Coordinates2D(1, 0), // vertical
            new Coordinates2D(0, 1), // horizontal
            new Coordinates2D(-1, 1), // left down to right up
            new Coordinates2D(1, 1) // left up to right down
    };

    /**
     * 2D-Array representing the board with a slot either being Human, Machine
     * or Tie.
     */
    private Player[][] board = new Player[ROWS][COLS];

    /**
     * The current difficulty level and search depth for the game tree.
     */
    private int level = 4;

    /**
     * The player to move.
     */
    private Player firstPlayer;

    /**
     * The collection containing possible witnesses.
     * If there are {@code CONNECT} amount of tokens connected, contains
     * the winning group, otherwise it is empty.
     */
    private Collection<Coordinates2D> witness = Collections.emptySet();

    /**
     * The machine's evaluation of the current state.
     */
    private int evaluation;

    /**
     * Arrays containing the count of connected groups either player has
     * respectively, where the count of n sized groups is saved at index n - 2.
     */
    private int[] humanGroups = new int[CONNECT - 1];
    private int[] machineGroups = new int[CONNECT - 1];


    /**
     * Constructs an empty GameState, without any tokens set.
     */
    public GameState() {
        Arrays.stream(board).forEach(row -> Arrays.fill(row, Player.TIE));
    }

    /**
     * {@inheritDoc}
     *
     * @param col The column where to put the token of the human.
     * @return A new board with the move executed. If the move is not valid,
     * i.e. {@code col} was full before, then {@code null} will be
     * returned.
     * @throws IllegalMoveException     The game is already over, or it is not
     *                                  the human's turn.
     * @throws IllegalArgumentException The provided column {@code col} is
     *                                  invalid, i.e. not found on the grid.
     */
    public Board move(int col) {
        if (firstPlayer == null) {
            firstPlayer = Player.HUMAN;
        }

        if (isGameOver()) {
            throw new IllegalMoveException("The game is over!");
        } else if (firstPlayer != Player.HUMAN) {
            throw new IllegalMoveException("Its not your turn!");
        } else if (col < 0 || col > COLS - 1) {
            throw new IllegalArgumentException("Column out of bounds!");
        } else {
            GameState newBoard = (GameState) clone();
            newBoard.firstPlayer = Player.MACHINE;

            if (!newBoard.insertToken(col, Player.HUMAN)) {
                newBoard = null;
            }
            return newBoard;
        }
    }

    /**
     * Executes a machine move. This method does not change the state of this
     * instance, which is treated here as immutable. Instead, a new board/game
     * is returned, which is a copy of {@code this} with the move executed.
     * <p>
     * The best move is calculated by creating a game tree containing all
     * possible moves up to a depth of {@link GameState#level}.
     * Then the minmax algorithm is used to determine the best
     * move the machine can make. This will be the move the machine executes.
     *
     * @return A new board with the move executed or {@code null},
     * when there was an error inserting the token
     * @throws IllegalMoveException The game is already over, or it is not
     *                              the machine's turn.
     */
    public Board machineMove() {
        if (firstPlayer == null) {
            firstPlayer = Player.MACHINE;
        }
        if (isGameOver()) {
            throw new IllegalMoveException("The game is over!");
        } else if (firstPlayer != Player.MACHINE) {
            throw new IllegalMoveException("It's not the machine's turn");
        }

        // creating the game tree
        Node root = new Node(level, evaluation, COLS);
        buildSubtree(this, root, level - 1);

        int col = root.getIndexOfMaxChild();

        GameState newBoard = (GameState) clone();
        newBoard.firstPlayer = Player.HUMAN;
        if (!newBoard.insertToken(col, Player.MACHINE)) {
            newBoard = null;
        }
        return newBoard;
    }

    /**
     * Inserts a token of the given player into the given column.
     * Immediately evaluates the board,
     * because this instance is being treated as immutable,
     * therefore after inserting a token no changes to the evaluation can occur.
     *
     * @param col    the column, the token gets inserted into starting from 0.
     * @param player the player whose token gets inserted.
     * @return true if the operation was successful, false otherwise.
     */
    private boolean insertToken(int col, Player player) {
        if (col < 0 || col > COLS - 1) {
            return false;
        }

        int row = ROWS - 1;
        while (board[row][col] != Player.TIE && row > 0) {
            row--;
        }

        if (board[row][col] == Player.TIE) {
            board[row][col] = player;
            countGroups();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Recursively builds the subtree of a given {@link Node} up to a given
     * depth. Each inner node has a maximum number of {@link Board#COLS}
     * children.
     * These children represent every possible move.
     *
     * @param gameState the gameState of the parent
     * @param parent    the parent node
     * @param depth     the depth of the subtree
     */
    private void buildSubtree(GameState gameState, Node parent, int depth) {
        if (depth < 0 || gameState == null) {
            return;
        }

        for (int i = 0; i < COLS; i++) {
            GameState newGameState = (GameState) gameState.clone();

            if (newGameState.insertToken(i, newGameState.firstPlayer)) {
                newGameState.firstPlayer
                        = Player.oppositePlayer(newGameState.firstPlayer);
                Node child = new Node(
                        depth, newGameState.evaluateBoard(depth), COLS);
                parent.setChild(child, i);

                buildSubtree(newGameState, child, depth - 1);
            }
        }
    }

    /**
     * Contains the machine's evaluation formula for boards of standard size.
     * First calculates the groups for each player,
     * then calculates the number of tokens in each column.
     * Finally, checks if the machine can win with its next move.
     *
     * @param depth the current depth in the game tree
     * @return Evaluation value of this instance.
     */
    private int evaluateBoard(int depth) {
        int p = 50 + machineGroups[0] + 4 * machineGroups[1]
                + 5000 * machineGroups[2] - humanGroups[0]
                - 4 * humanGroups[1] - 500000 * humanGroups[2];

        int q = getTokensInCol(1, Player.MACHINE)
                + 2 * getTokensInCol(2, Player.MACHINE)
                + 3 * getTokensInCol(3, Player.MACHINE)
                + 2 * getTokensInCol(4, Player.MACHINE)
                + getTokensInCol(5, Player.MACHINE)
                - getTokensInCol(1, Player.HUMAN)
                - 2 * getTokensInCol(2, Player.HUMAN)
                - 3 * getTokensInCol(3, Player.HUMAN)
                - 2 * getTokensInCol(4, Player.HUMAN)
                - getTokensInCol(5, Player.HUMAN);

        int r = 0;
        if (getWinner() == Player.MACHINE
                && firstPlayer == Player.MACHINE
                && depth == level - 1) {
            r = 5000000;
        }

        return p + q + r;
    }

    /**
     * Counts the tokens in a given column for a given player.
     *
     * @param col    the col to be checked.
     * @param player the player whose tokens get counted.
     * @return the number of tokens in the column.
     */
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
     * Loops over every entry in {@link GameState#START_COORDINATES} and
     * calculates the groups for every needed direction for that entry, by
     * calling {@link #calcGroupsInLine} with the corresponding vector
     * from {@link GameState#DIRECTION_VECTORS}.
     */
    private void countGroups() {
        // reset groups
        Arrays.fill(humanGroups, 0);
        Arrays.fill(machineGroups, 0);

        int diagCount = ROWS + COLS - 1;
        for (int i = 0; i < START_COORDINATES.length; i++) {
            if (i <= diagCount) {
                // from left down corner to right up corner
                calcGroupsInLine(START_COORDINATES[i], DIRECTION_VECTORS[2]);
            }
            if (i >= COLS - 1 && i <= diagCount) {
                // horizontal
                calcGroupsInLine(START_COORDINATES[i], DIRECTION_VECTORS[1]);
            }
            if (i >= COLS - 1) {
                // from left upper corner to right down corner
                calcGroupsInLine(START_COORDINATES[i], DIRECTION_VECTORS[3]);
            }
            if (i >= diagCount - 1) {
                // vertical
                calcGroupsInLine(START_COORDINATES[i], DIRECTION_VECTORS[0]);
            }
        }
    }

    /**
     * Calculates the groups of both players in a given line.
     * Lines can be vertical, horizontal, diagonal up or diagonal down.
     * <p>
     * Traverses the board from the given {@code start} in the given direction
     * until it runs out of bounds.
     * While traversing increases the respective entry in
     * {@link GameState#humanGroups} and {@link GameState#machineGroups} by
     * calling {@link GameState#increaseGroupCount}.
     * <p>
     * When the count reaches {@link Board#CONNECT} i.e a player has a winning
     * connection, saves the winning group into {@link GameState#witness}.
     *
     * @param start the start coordinate.
     * @param incr  the direction vector.
     */
    private void calcGroupsInLine(Coordinates2D start, Coordinates2D incr) {
        int count = 0;
        int row = start.row();
        int col = start.col();
        Player prevPlayer = board[row][col];
        Set<Coordinates2D> tempWitness = new TreeSet<>();

        // loop over every coordinate in one line/diag
        while (isInBounds(row, col)) {

            if (board[row][col] == Player.TIE) {
                increaseGroupCount(count, prevPlayer);
                count = 1;
            } else if (board[row][col] == prevPlayer) {
                count++;
                tempWitness.add(new Coordinates2D(row, col));
            } else {
                increaseGroupCount(count, prevPlayer);
                count = 1;
                tempWitness.clear();
                tempWitness.add(new Coordinates2D(row, col));
            }

            if (count >= CONNECT) {
                increaseGroupCount(count, prevPlayer);
                witness.clear();
                witness.addAll(tempWitness);
                tempWitness.clear();
                count = 0;
            }

            prevPlayer = board[row][col];
            row += incr.row();
            col += incr.col();

        }
        increaseGroupCount(count, prevPlayer);
    }

    /**
     * Increases the corresponding entry in {@link GameState#humanGroups}
     * and {@link GameState#machineGroups} by one if count is more than 2.
     *
     * @param count  the size of the group.
     * @param player the player whose group it is.
     */
    private void increaseGroupCount(int count, Player player) {
        if (count == 0 || count == 1) {
            return;
        }
        int index = Math.min(CONNECT - 2, count - 2);
        if (player == Player.HUMAN) {
            humanGroups[index]++;
        } else if (player == Player.MACHINE) {
            machineGroups[index]++;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return A clone.
     */
    public Board clone() {
        GameState copy = null;
        try {
            copy = (GameState) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        Player[][] boardCopy = new Player[board.length][];
        for (int i = 0; i < board.length; i++) {
            boardCopy[i] = Arrays.copyOf(board[i], board[i].length);
        }

        assert copy != null;
        copy.board = boardCopy;
        copy.level = level;
        copy.firstPlayer = firstPlayer;
        copy.witness = new TreeSet<>(witness);
        copy.evaluation = evaluation;
        copy.humanGroups = Arrays.copyOf(humanGroups, humanGroups.length);
        copy.machineGroups
                = Arrays.copyOf(machineGroups, machineGroups.length);

        return copy;
    }

    /**
     * {@inheritDoc}
     *
     * @return The string representation of the current Connect Four board.
     */
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        for (Player[] players : board) {
            for (int i = 0; i < board[0].length; i++) {
                strBuilder.append(players[i].toString()).append(" ");
            }
            // delete trailing whitespace
            strBuilder.setLength(strBuilder.length() - 1);
            strBuilder.append("\n");
        }
        strBuilder.setLength(strBuilder.length() - 1);
        return strBuilder.toString();
    }

    /**
     * {@inheritDoc}
     */
    public void setLevel(int level) {
        if (level < 1 || level > MAX_LEVEL) {
            throw new IllegalArgumentException(
                    "The level must be between 1 and " + MAX_LEVEL + "!");
        } else {
            this.level = level;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return The list of coordinates.
     * @throws IllegalStateException There is no winner available.
     */
    public Collection<Coordinates2D> getWitness() {
        Player player = getWinner();
        if (player == null) {
            throw new IllegalStateException("There is no winner available");
        }
        return witness;
    }

    /**
     * {@inheritDoc}
     *
     * @return The winner or {@code null} in case of a tie or if the game is
     * not finished yet.
     */
    public Player getWinner() {
        if (humanGroups[CONNECT - 2] != 0) {
            return Player.HUMAN;
        } else if (machineGroups[CONNECT - 2] != 0) {
            return Player.MACHINE;
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param row The row of the slot in the game grid.
     * @param col The column of the slot in the game grid.
     * @return The slot's content.
     * @throws IllegalArgumentException The coordinates are out of bounds.
     */
    public Player getSlot(int row, int col) {
        if (!isInBounds(row, col)) {
            throw new IllegalArgumentException("Parameters out of bounds");
        }
        return board[row][col];
    }

    /**
     * {@inheritDoc}
     *
     * @return The player who makes the initial move.
     */
    public Player getFirstPlayer() {
        return firstPlayer;
    }

    /**
     * Checks if game is over. Either one player has won or there is a tie
     * and all slots are filled with tokens.
     *
     * @return {@code true} if and only if the game is over.
     */
    public boolean isGameOver() {
        return isAllSlotsFilled() || getWinner() != null;
    }

    /**
     * Checks if a given coordinate is in bounds of the used board.
     *
     * @param row the row to check.
     * @param col the col to check.
     * @return true when the coordinates are in bounds, false otherwise.
     */
    private boolean isInBounds(int row, int col) {
        return row <= ROWS - 1 && row >= 0
                && col <= COLS - 1 && col >= 0;
    }

    /**
     * Checks if there are empty slots left to be filled by a player.
     *
     * @return false if there are free slots, true if all slots are filled.
     */
    private boolean isAllSlotsFilled() {
        for (int i = 0; i < board[0].length; i++) {
            if (board[0][i] == Player.TIE) {
                return false;
            }
        }
        return true;
    }

}
