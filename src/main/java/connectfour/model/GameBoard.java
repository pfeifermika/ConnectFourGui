package connectfour.model;

import java.util.Collection;

public class GameBoard implements Board {

    // 1 is human; 2 is computer; empty is 0;
    public int[][] board;
    int level = 4;
    Player firstPlayer = Player.HUMAN;

    public GameBoard() {
        this.board = new int[ROWS][COLS];
    }

    public GameBoard(int[][] board, int level, Player firstPlayer) {
        this.board = board;
        this.level = level;
        this.firstPlayer = firstPlayer;
    }

    public int[] addGroups(Player player) {
        int[] vertical = countVerticalGroups(player);
        int[] horizontal = countHorizontalGroups(player);
        int[] result = new int[vertical.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = vertical[i] + horizontal[i]; //+ diagonalUp[i] + diagonalDown[i];
        }
        return result;
    }

    public int[] countHorizontalGroups(Player player) {
        int[] result = new int[CONNECT - 1];    //due to 1 not being counted
        int playerId = player.id;
        for (int i = 0; i < board.length; i++) {
            int count = 0;
            for (int k = 0; k < board[0].length; k++) {
                if (board[i][k] == playerId) {
                    count++;
                } else if (count >= CONNECT) {
                    result[CONNECT - 2]++;
                    count = 0;
                } else if (count > 1) {
                    result[count - 2]++;
                    count = 0;
                }
            }
            if (count >= CONNECT) {
                result[CONNECT - 2]++;
            } else if (count > 1) {
                result[count - 2]++;
            }
        }
        return result;
    }

    public int[] countVerticalGroups(Player player) {
        int[] result = new int[CONNECT - 1];    //due to 1 not being counted
        int playerId = player.id;
        for (int i = 0; i < board[0].length; i++) {
            int count = 0;
            for (int k = 0; k < board.length; k++) {
                if (board[k][i] == playerId) {
                    count++;
                } else if (count >= CONNECT) {
                    result[CONNECT - 2]++;
                    count = 0;
                } else if (count > 1) {
                    result[count - 2]++;
                    count = 0;
                }
            }
            if (count >= CONNECT) {
                result[CONNECT - 2]++;
            } else if (count > 1) {
                result[count - 2]++;
            }
        }
        return result;
    }

    public int[] countTokensInColumn(Player player) {
        int[] result = new int[COLS - 2];
        int count = 0;
        for (int i = 0; i < board[0].length - 1; i++) {
            for (int k = 0; k < board.length; k++) {
                if (board[k][i + 1] == player.id) {
                    count++;
                }
            }
            result[i] = count;
            count = 0;
        }
        return result;
    }

    public int evaluateBoard() {
        int[] groupsComputer = addGroups(Player.COMPUTER);
        int[] groupsHuman = addGroups(Player.HUMAN);

        int p = 50 + groupsComputer[0] + 4 * groupsComputer[1] + 5000 * groupsComputer[2] - groupsHuman[0] - 4 * groupsHuman[1] - 500000 * groupsHuman[2];

        int[] colsComputer = countTokensInColumn(Player.COMPUTER);
        int[] colsHuman = countTokensInColumn(Player.HUMAN);

        int m2 = colsComputer[0];
        int m3 = colsComputer[1];
        int m4 = colsComputer[2];
        int m5 = colsComputer[3];
        int m6 = colsComputer[4];

        int h2 = colsHuman[0];
        int h3 = colsHuman[1];
        int h4 = colsHuman[2];
        int h5 = colsHuman[3];
        int h6 = colsHuman[4];

        int q = m2 + 2 * m3 + 3 * m4 + 2 * m5 + m6 - h2 - 2 * h3 - 3 * h4 - 2 * h5 - h6;

        int r = 0;
        if (getWinner() == Player.COMPUTER) {
            r = 5000000;
        }

        return p + q + r;

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
        int col = 2; //TODO get eval

        GameBoard newBoard = (GameBoard) clone();
        newBoard.insertChip(col - 1, Player.COMPUTER);
        newBoard.setFirstPlayer(Player.HUMAN);
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


//--------------------------------------------------------------------------------------------------






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
        return getWinner() != null || isAllSlotsFilled();
    }

    private boolean isAllSlotsFilled(){
        for (int i = 0; i < board[0].length; i++) {
            if(board[0][i] == 0){
                return false;
            }
        }
        return true;
    }


    /**
     * Checks if the game state is won.
     *
     * @return The winner or {@code null} in case of a tie or if the game is
     * not finished yet.
     */
    public Player getWinner() {
        int[] humanGroups = addGroups(Player.HUMAN);
        int[] computerGroups = addGroups(Player.COMPUTER);
        Player winner;

        if (humanGroups[humanGroups.length - 1] > 0) {
            winner = Player.HUMAN;
        } else if (computerGroups[computerGroups.length - 1] > 0) {
            winner = Player.COMPUTER;
        } else {
            winner = null;
        }
        return winner;
    }



    /**
     * Executes a human move. This method does not change the state of this
     * instance, which is treated here as immutable. Instead, a new board/game
     * is returned, which is a copy of {@code this} with the move executed.
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
        if (isGameOver() || firstPlayer.equals(Player.HUMAN)) {
            throw new IllegalMoveException("The game is over or its not your turn!");
        } else if (col < 1 || col > COLS) {
            throw new IllegalArgumentException("Argument out of bounds!");
        } else if (board[0][col - 1] != 0) {
            return null;
        } else {
            GameBoard newBoard = (GameBoard) clone();
            newBoard.insertChip(col - 1, Player.HUMAN);
            newBoard.setFirstPlayer(Player.COMPUTER);
            return newBoard;
        }
    }

    public void insertChip(int col, Player player) {
        int i = board.length - 1;
        while (board[i][col] != 0) {
            i--;
        }
        board[i][col] = player.id;
    }

    public void setFirstPlayer(Player newFirstPlayer) {
        firstPlayer = newFirstPlayer;
    }

    /**
     * Sets the skill level of the machine.
     *
     * @param level The skill as number, must be at least 1.
     */
    public void setLevel(int level) {
        if (level > 1) {
            this.level = level;
        }
    }

    /**
     * Gets the content of the slot at the specified coordinates. Either it
     * contains a tile of one of the two players already or it is empty.
     *
     * @param row The row of the slot in the game grid.
     * @param col The column of the slot in the game grid.
     * @return The slot's content.
     */
    public Player getSlot(int row, int col) {
        int result = board[row][col];
        return switch (result) {
            case 0 -> null;
            case 1 -> Player.HUMAN;
            case 2 -> Player.COMPUTER;
            default ->
                    //TODO ERROR MESSAGE
                    null;
        };
    }

    /**
     * Creates and returns a deep copy of this board.
     *
     * @return A clone.
     */
    public Board clone() {
        int[][] clonedBoard = new int[board.length][];
        for (int i = 0; i < board.length; i++)
            clonedBoard[i] = board[i].clone();
        Player newFirstPlayer = this.getFirstPlayer();
        return new GameBoard(clonedBoard, level, newFirstPlayer);
    }

    /**
     * Gets the string representation of this board as row x column matrix. Each
     * slot is represented by one the three chars '.', 'X', or 'O'. '.' means
     * that the slot currently contains no tile. 'X' means that it contains a
     * tile of the human player. 'O' means that it contains a machine tile. In
     * contrast to the rows, the columns are whitespace separated.
     *
     * @return The string representation of the current Connect Four game.
     */
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < board.length; i++) {
            for (int k = 0; k < board[0].length; k++) {
                switch (board[i][k]) {
                    case 1 -> strBuilder.append("X ");
                    case 2 -> strBuilder.append("O ");
                    default -> strBuilder.append(". ");
                }
            }
            strBuilder.append("\n");
        }
        return strBuilder.toString();
    }

}
