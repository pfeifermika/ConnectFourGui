package connectfour.model;

import connectfour.model.exceptions.IllegalMoveException;
import connectfour.model.utility.BoardUtility;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a state of a game of ConnectFour.
 * <p>
 * An instance of this Class is treated as immutable, therefore a new instance
 * with the move executed is returned, when calling {@link GameState#move(int)}
 * or {@link GameState#machineMove()}.
 * <p>
 * This class implements methods to execute a human or computer move,
 * to change the difficulty level, to get the player to move, to check if the
 * game is over, to check if there is a winner, to get the witnesses of a win
 * and the player token of a specific slot.
 * <p>
 * The top left corner of {@code board} is the coordinate (0,0)
 * <p>
 * Implements public methods to play a game of ConnectFour against a computer.
 */
public class GameState implements Board {

    /**
     * 2D-Array representing the board with either slot being Human, Computer or
     * Tie.
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
     * If there are {@code CONNECT} amount of chips connected contains
     * the winning group otherwise it is empty
     */
    private Collection<Coordinates2D> witness;
    /**
     * The computer evaluation of the current state.
     */
    private int evaluation;

    /**
     * Arrays containing the count of connected groups either player has
     * respectively, where the count of n-groups is saved at index n - 2.
     */
    private final int[] humanGroups;
    private final int[] computerGroups;

    /**
     * Contains the starting coordinates used to calculate the groups.
     */
    private static final Coordinates2D[] START_COORDINATES
            = BoardUtility.calculateStartCoordinates();

    /**
     * Direction vectors representing different
     * directions to traverse the board when calculating groups.
     */
    private static final Coordinates2D[] DIRECTION_VECTORS = {
            new Coordinates2D(1, 0), // vertical
            new Coordinates2D(0, 1), // horizontal
            new Coordinates2D(-1, 1), // diag up
            new Coordinates2D(1, 1) // diag down
    };

    /**
     * Constructs a blank gamestate, without any tokens.
     */
    public GameState() {
        Arrays.stream(board).forEach(row -> Arrays.fill(row, Player.TIE));
        humanGroups = new int[CONNECT - 1];
        computerGroups = new int[CONNECT - 1];
        witness = new TreeSet<>();
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
     * {@inheritDoc}
     *
     * @param col The column where to put the tile of the human.
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

        if (isGameOver() || firstPlayer != Player.HUMAN) {
            throw new IllegalMoveException(
                    "The game is over or its not your turn!");
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
     * Executes a machine move. This method does not change the state of this
     * instance, which is treated here as immutable. Instead, a new board/game
     * is returned, which is a copy of {@code this} with the move executed.
     *
     * @return A new board with the move executed or {@code null},
     * when there was an error inserting the chip
     * @throws IllegalMoveException The game is already over, or it is not
     *                              the machine's turn.
     */
    public Board machineMove() {
        if (firstPlayer == null) {
            firstPlayer = Player.COMPUTER;
        }
        if (isGameOver() || firstPlayer != Player.COMPUTER) {
            throw new IllegalMoveException();
        }

        // creating the game tree
        Node root = new Node(level, evaluation);
        buildSubtree(this, root, level - 1);

        int col = root.getBestMove();

        GameState newBoard = (GameState) clone();
        newBoard.firstPlayer = Player.HUMAN;
        if (!newBoard.insertChip(col, Player.COMPUTER)) {
            newBoard = null;
        }
        return newBoard;
    }

    /**
     * {@inheritDoc}
     */
    public void setLevel(int level) {
        if (level > 0) {
            this.level = level;
        } else {
            throw new IllegalArgumentException(
                    "The level must not be lower than 1");
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code true} if and only if the game is over.
     */
    public boolean isGameOver() {
        return isAllSlotsFilled() || getWinner() != null;
    }

    /**
     * {@inheritDoc}
     *
     * @return The winner or {@code null} in case of a tie or if the game is
     * not finished yet.
     */
    public Player getWinner() {
        if (humanGroups[2] != 0) {
            return Player.HUMAN;
        } else if (computerGroups[2] != 0) {
            return Player.COMPUTER;
        } else {
            return null;
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
     * @return A clone.
     */
    public Board clone() {
        GameState copy = null;
        try {
            copy = (GameState) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        Collection<Coordinates2D> clonedWitness = new TreeSet<>(witness);

        Player[][] clonedArray = new Player[board.length][];
        for (int i = 0; i < board.length; i++) {
            clonedArray[i] = board[i].clone();
        }

        assert copy != null;
        copy.board = clonedArray;
        copy.firstPlayer = this.getFirstPlayer();
        copy.level = this.level;
        copy.witness = clonedWitness;
        copy.evaluation = evaluation;

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
            for (int k = 0; k < board[0].length; k++) {
                strBuilder.append(players[k].toString()).append(" ");
            }
            strBuilder.append("\n");
        }
        return strBuilder.toString();
    }


    /**
     * Inserts a chip of the given player into the given slot.
     * Also evaluates the board,
     * due to this instance being treated as immutable,
     * therefore after inserting a chip no changes to the evaluation can occur.
     *
     * @param col    the column, the chip gets inserted into starting from 0.
     * @param player the player whose chip gets inserted.
     * @return true if the operation was successful, false otherwise.
     */
    private boolean insertChip(int col, Player player) {
        if (col < 0 || col > COLS) {
            return false;
        }

        int i = ROWS - 1;
        while (board[i][col] != Player.TIE && i > 0) {
            i--;
        }

        if (board[i][col] == Player.TIE) {
            board[i][col] = player;
            evaluation = evaluateBoard();
            return true;
        } else {
            return false;
        }
    }


    /**
     * Recursively builds the subtree of a given {@link Node} up to a given
     * depth. Each inner node has at maximum {@code COLS} number of children.
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

            if (newGameState.insertChip(i, newGameState.firstPlayer)) {
                Node child = new Node(depth, newGameState.evaluation);
                newGameState.firstPlayer
                        = Player.getOtherPlayer(newGameState.getFirstPlayer());
                parent.setChild(child, i);

                buildSubtree(newGameState, child, depth - 1);
            }
        }
    }

    /**
     * Contains the computer evaluation formula for boards of standard size.
     * First calculates the groups for each player,
     * then calculates the number of tokens in each column.
     * Finally, checks if the computer can win with its next move.
     *
     * @return evaluation of this instance.
     */
    private int evaluateBoard() {
        // reset groups
        Arrays.fill(humanGroups, 0);
        Arrays.fill(computerGroups, 0);

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
        if (getWinner() == Player.COMPUTER && firstPlayer == Player.COMPUTER) {
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
     * {@link GameState#humanGroups} and {@link GameState#computerGroups} by
     * calling {@link GameState#increaseGroupCount}.
     * <p>
     * When the count reaches 4 i.e a player has a winning connection,
     * saves the winning group into {@link GameState#witness}.
     *
     * @param start the start coordinate.
     * @param incr  the direction vector.
     */
    private void calcGroupsInLine(Coordinates2D start, Coordinates2D incr) {
        int count = 0;
        int row = start.row();
        int col = start.col();
        Player player = board[row][col];
        Set<Coordinates2D> tempWitness = new TreeSet<>();

        // loop over every coordinate in one line/diag
        while (isInBounds(row, col)) {

            if (player == Player.TIE) {
                count = 1;
            } else if (board[row][col] == player) {
                count++;
                tempWitness.add(new Coordinates2D(row, col));
            } else {
                increaseGroupCount(count, player);
                count = 1;
                tempWitness.clear();
            }

            if (count >= CONNECT) {
                increaseGroupCount(count, player);
                witness.clear();
                witness.addAll(tempWitness);
                tempWitness.clear();
                count = 0;
            }

            player = board[row][col];
            row += incr.row();
            col += incr.col();
        }
        increaseGroupCount(count, player);
    }

    /**
     * Increases the corresponding entry in {@link GameState#humanGroups}
     * and {@link GameState#computerGroups} by one.
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
        } else if (player == Player.COMPUTER) {
            computerGroups[index]++;
        }
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
     * @return true if there are empty slots.
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
