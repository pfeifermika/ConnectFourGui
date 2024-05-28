package connectfour.gui;

import connectfour.model.Board;
import connectfour.model.Coordinates2D;
import connectfour.model.GameState;
import connectfour.model.Player;
import connectfour.model.exceptions.IllegalMoveException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Collections;


/**
 * Visual representation of a grid in a game of Connect four.
 * <p>
 * Provides methods to handle user input through clicking on the board or
 * external buttons.
 */
public class GameBoardPanel extends JPanel {

    /**
     * 2d array containing all Tokens at their respective position for easier
     * access.
     */
    private final Token[][] tokens = new Token[Board.ROWS][Board.COLS];
    /**
     * Listener instance to register user input.
     */
    private final MouseListener mouseListener = new MouseListener();
    /**
     * Thread to calculate the machine move.
     */
    private Thread machineMoveThread;
    /**
     * Current state of the game.
     */
    private Board currentGameState;
    /**
     * Flag indicating if the machine is calculating.
     */
    private boolean machineIsCalculating = false;
    /**
     * Current level of the game.
     */
    private int level = 4;
    /**
     * The starting player of the game currently running.
     */
    private Player firstPlayer = Player.HUMAN;

    /**
     * Constructs a new GameBoardPanel.
     * <p>
     * Initializes the model in the default state and adds all the tokens to the
     * board.
     */
    protected GameBoardPanel() {
        super();
        currentGameState = new GameState();
        setLayout(new GridLayout(Board.ROWS, Board.COLS));
        addTokensToBoard();
    }

    /**
     * MouseListener to handle user input.
     * Handles only to the mousePressed event.
     */
    private class MouseListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            Object source = e.getSource();
            if (source instanceof Token) {
                tokenClicked(((Token) source).getPosition());
            }
        }
    }

    /**
     * Adds blank tokens to the {@code JPanel} and to the
     * {@link GameBoardPanel#tokens} array at the location corresponding
     * to their location on the grid.
     */
    private void addTokensToBoard() {
        for (int i = 0; i < Board.ROWS; i++) {
            for (int j = 0; j < Board.COLS; j++) {
                Token token = new Token(new Coordinates2D(i, j));
                token.addMouseListener(mouseListener);
                add(token);
                tokens[i][j] = token;
            }
        }
    }

    /**
     * Called when a {@link Token} is clicked. The position of the {@link Token}
     * on the board is given as parameter.
     * <p>
     * Checks if the click was invalid, meaning the game is over or the
     * machine is still calculating. Then tries to execute a move in the column
     * of {@code position}.
     * <p>
     * If there is an error making the move, displays a popup window containing
     * detailed information regarding the nature of the error.
     * <p>
     * Automatically checks if the game is over after executing the move.
     * If the game is not over executes a machine move, otherwise shows a popup
     * stating the winner.
     *
     * @param position the position of the clicked token on the grid
     */
    protected void tokenClicked(Coordinates2D position) {
        if (currentGameState.isGameOver()) {
            displayErrorPopup("The game is over. You cannot move!");
            return;
        } else if (machineIsCalculating) {
            displayErrorPopup("The machine is still calculating...");
            return;
        }

        Board newGameState;
        try {
            newGameState = currentGameState.move(position.col());
        } catch (IllegalMoveException e) {
            displayErrorPopup("It's not your turn.");
            return;
        } catch (IllegalArgumentException e) {
            displayErrorPopup("Error while processing input.");
            return;
        }

        if (newGameState == null) {
            displayErrorPopup("The column was already full");
        } else {
            currentGameState = newGameState;
            repaintBoard();

            if (newGameState.isGameOver()) {
                displayWinnerPopup();
            } else {
                machineMove();
            }
        }
    }

    /**
     * Updates the viewed board to show the correct internal state of the model.
     * <p>
     * Calls the {@link Token#updateToken(Player, boolean)} method on every
     * token with the current information to display. Each {@link Token} then
     * decides individually, if it has to repaint itself,
     * based on the information given.
     */
    private void repaintBoard() {
        Collection<Coordinates2D> witness = Collections.emptySet();
        if (currentGameState.getWinner() != null) {
            try {
                witness = currentGameState.getWitness();
            } catch (IllegalStateException e) {
                displayErrorPopup("Internal error displaying witness");
            }
        }

        for (int row = 0; row < Board.ROWS; row++) {
            for (int col = 0; col < Board.COLS; col++) {
                boolean isWitness
                        = witness.contains(new Coordinates2D(row, col));
                Token tok = tokens[row][col];
                tok.updateToken(currentGameState.getSlot(row, col), isWitness);
            }
        }
    }

    /**
     * Executes a machine move. To allow the user to still be able to interact
     * with the user interface the costly calculation is done in a separate
     * {@code Thread}.
     */
    private void machineMove() {
        machineMoveThread = new Thread(() -> {
            Board newGameState;
            try {
                newGameState = currentGameState.machineMove();
            } catch (InterruptedException ignored) {
                machineIsCalculating = false;
                return;
            } catch (IllegalMoveException e) {
                displayErrorPopup("Error while calculating the machine's move");
                machineIsCalculating = false;
                return;
            }

            if (newGameState == null) {
                displayErrorPopup("Error inserting token.");
            } else {
                currentGameState = newGameState;
                repaintBoard();
                if (newGameState.isGameOver()) {
                    displayWinnerPopup();
                }
            }
            machineIsCalculating = false;
        });
        machineIsCalculating = true;
        machineMoveThread.start();
    }

    /**
     * Changes the current level.
     *
     * @param newLevel the new level.
     */
    protected void levelChanged(int newLevel) {
        currentGameState.setLevel(newLevel);
        level = newLevel;
    }

    /**
     * Terminates the program.
     * <p>
     * Interrupts the machine move calculation if ongoing and
     */
    protected void quitButtonPressed() {
        if (machineMoveThread.isAlive()) {
            machineMoveThread.interrupt();
        }
        JFrame window = (JFrame) SwingUtilities.getWindowAncestor(
                GameBoardPanel.this);

        window.setVisible(false);
        window.dispose();
    }

    /**
     * Switches the {@link GameBoardPanel#firstPlayer}starts a new game.
     * If the new {@link GameBoardPanel#firstPlayer} is the machine,
     * automatically executes a machine move.
     * <p>
     * Interrupts the machine move calculation if ongoing.
     */
    protected void switchButtonPressed() {
        if (machineMoveThread.isAlive()) {
            machineMoveThread.interrupt();
        }
        currentGameState = new GameState();
        currentGameState.setLevel(level);
        repaintBoard();
        if (firstPlayer == Player.HUMAN) {
            firstPlayer = Player.MACHINE;
            machineMove();
        } else if (firstPlayer == Player.MACHINE) {
            firstPlayer = Player.HUMAN;
        }
    }

    /**
     * Starts a new game. In case the machine made the first move, a machine
     * move gets triggered automatically.
     * <p>
     * Interrupts the machine move calculation if ongoing
     */
    protected void newButtonPressed() {
        if (machineMoveThread.isAlive()) {
            machineMoveThread.interrupt();
        }
        currentGameState = new GameState();
        currentGameState.setLevel(level);
        repaintBoard();
        if (firstPlayer == Player.MACHINE) {
            machineMove();
        }
    }

    /**
     * Displays a popup window, containing a given error message.
     *
     * @param message the error message to be displayed
     */
    private void displayErrorPopup(String message) {
        JOptionPane.showMessageDialog(this, message,
                "Error!", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays a popup window with information about the end of the game.
     * The window will only be shown if the
     * {@link GameBoardPanel#currentGameState} is a finished game.
     */
    private void displayWinnerPopup() {
        if (currentGameState.isGameOver()) {
            Player player = currentGameState.getWinner();
            String message;
            if (player == Player.HUMAN) {
                message = "Congratulations! You won.";
            } else if (player == Player.MACHINE) {
                message = "Sorry! Machine wins.";
            } else {
                message = "Nobody wins. Tie.";
            }
            JOptionPane.showMessageDialog(this, message,
                    "The game is over!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}