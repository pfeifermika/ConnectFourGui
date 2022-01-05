package connectfour.shell;

import connectfour.model.Board;
import connectfour.model.Coordinates2D;
import connectfour.model.GameState;
import connectfour.model.Player;
import connectfour.model.exceptions.IllegalMoveException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;


/**
 * Utility class, managing user input and console output, to play a game of
 * connect four.
 */
public final class Shell {

    /**
     * The current state of the game.
     */
    private static Board game;
    /**
     * The starting player of the game currently running.
     */
    private static Player firstPlayer = Player.HUMAN;
    /**
     * The difficulty level of the game currently running.
     */
    private static int level = 4;

    /**
     * Private constructor to ensure non non-instantiability.
     */
    private Shell() {
        throw new AssertionError("Utility Class!");
    }

    /**
     * Main game loop.
     * <p>
     * Initializes a new game and waits for user input.
     * Keeps processing user input until the program is terminated by either
     * entering 'quit' or {@code null} input.
     *
     * @param args Command line arguments.
     * @throws IOException If an I/O error occurs while reading user input.
     */
    public static void main(String[] args) throws IOException {
        BufferedReader stdin
                = new BufferedReader(new InputStreamReader(System.in));
        game = new GameState();
        boolean run = true;

        while (run) {
            System.out.print("connect4> ");
            String input = stdin.readLine();
            if (input == null) {
                break;
            }
            String[] tokens = input.trim().split("\\s+");

            switch (Character.toLowerCase(tokens[0].charAt(0))) {
                case 'n' -> newGame();
                case 'l' -> changeLevel(tokens);
                case 's' -> switchSides();
                case 'm' -> move(tokens);
                case 'w' -> printWitness();
                case 'p' -> System.out.println(game.toString());
                case 'h' -> printHelp();
                case 'q' -> run = false;
                default -> System.out.println("Error! Invalid command!");
            }
        }
    }

    /**
     * Starts a new game with the same difficulty and first player as the game
     * before. Standard is level 4 and human to move.
     */
    private static void newGame() {
        game = new GameState();
        game.setLevel(level);
        if (firstPlayer == Player.COMPUTER) {
            machineMove();
        }
    }

    /**
     * Changes the level to the entered level. If the input level is over 9
     * this will print an error message, because the game tree would be too big.
     *
     * @param tokens the user input
     */
    private static void changeLevel(String[] tokens) {
        try {
            int newLevel = Integer.parseInt(tokens[1]);
            if (newLevel > 6) {
                System.out.println("Error! The entered level is too high.");
            }
            game.setLevel(newLevel);
            level = newLevel;
        } catch (NumberFormatException | NullPointerException e) {
            System.out.println("Error! Invalid Arguments!");
        }
    }

    /**
     * Switches the {@link Shell#firstPlayer} and starts a new game.
     * If the new {@link Shell#firstPlayer} is the machine, automatically
     * executes a machine move.
     */
    private static void switchSides() {
        game = new GameState();
        game.setLevel(level);
        if (firstPlayer == Player.HUMAN) {
            firstPlayer = Player.COMPUTER;
            machineMove();
        } else if (firstPlayer == Player.COMPUTER) {
            firstPlayer = Player.HUMAN;
        }
    }

    /**
     * Executes a human move.
     * <p>
     * If any of the steps fails prints an error message with detailed
     * information regarding the nature of the error.
     * <p>
     * Automatically checks if the game is over after executing the move.
     * If the game is not over executes a machine move, otherwise prints out a
     * winner message.
     *
     * @param tokens the user input
     */
    private static void move(String[] tokens) {
        if (game.isGameOver()) {
            System.out.println("Error! The game is over!");
            return;
        }

        Board newGamestate;

        try {
            int column = Integer.parseInt(tokens[1]);
            newGamestate = game.move(column - 1);
        } catch (NumberFormatException | NullPointerException
                | ArrayIndexOutOfBoundsException e) {
            System.out.println("Error! Invalid Arguments!");
            return;
        } catch (IllegalArgumentException e) {
            System.out.println("Error! The column is out of bounds");
            return;
        } catch (IllegalMoveException e) {
            System.out.println("Error! Its not your turn");
            return;
        }

        if (newGamestate == null) {
            System.out.println("Error! The column was already full!");
        } else if (newGamestate.isGameOver()) {
            game = newGamestate;
            printWinnerMessage();
        } else {
            // game is not finished.
            game = newGamestate;
            machineMove();
        }

    }

    /**
     * Executes a machine move.
     * <p>
     * If any of the steps fails prints an error message with detailed
     * information regarding the nature of the error.
     * <p>
     * Automatically checks if the game is over after executing the move.
     * If the game is over prints out a winner message.
     */
    private static void machineMove() {
        if (game.isGameOver()) {
            System.out.println("Error! The game is over!");
            return;
        }

        Board newGamestate;

        try {
            newGamestate = game.machineMove();
        } catch (IllegalMoveException e) {
            System.out.println("Error! Its not the computers move!");
            return;
        } catch (InterruptedException e) {
            System.out.println(
                    "Error! Error while calculating the computer move.");
            return;
        }

        if (newGamestate == null) {
            System.out.println("Error! Error executing the calculated move.");
        } else if (newGamestate.isGameOver()) {
            game = newGamestate;
            printWinnerMessage();
        } else {
            game = newGamestate;
        }
    }

    /**
     * Prints a winner message stating if someone has won or if there is a tie.
     * If the game is not over does nothing.
     */
    private static void printWinnerMessage() {
        if (game.isGameOver()) {
            Player player = game.getWinner();

            if (player == Player.HUMAN) {
                System.out.println("Congratulations! You won.");
            } else if (player == Player.COMPUTER) {
                System.out.println("Sorry! Machine wins.");
            } else {
                System.out.println("Nobody wins. Tie.");
            }
        } else {
            throw new IllegalStateException("The game is not over.");
        }
    }

    /**
     * Prints a helpful message describing possible commands.
     */
    private static void printHelp() {
        System.out.print("""
                Commands:
                - new:     Starts a new game
                - level l: Sets the difficulty to 'l' (default is 4)
                               Only difficulties from 1 to 5 are supported.
                - switch:  Switches the starting player and starts a new game.
                - move c:  Drops a token in the column 'c'.
                - witness: Prints out the coordinates of a connect four group.
                               Only usable, when the game is won by any player.
                - print:   Prints the current board to the console.
                - help:    Prints out this help text.
                - quit:    Terminates the program.
                """);
    }

    /**
     * Prints out the coordinates of the winning connect four group.
     */
    private static void printWitness() {
        if (game.getWinner() != null) {
            StringJoiner joiner = new StringJoiner(", ");
            Set<Coordinates2D> set = new TreeSet<>();
            game.getWitness().forEach(c -> set.add(convertCord(c)));

            set.forEach(c ->
                    joiner.add(c.toString()));

            System.out.println(joiner);
        } else {
            System.out.println("Error! The game is not won by any player!");
        }
    }

    /**
     * Converts a {@link Coordinates2D} of a board where (0,0) is in the top
     * left corner to a {@link Coordinates2D} of a board, where (1,1) is in the
     * bottom left corner.
     *
     * @param cord the coordinate to be converted
     * @return the converted coordinate.
     */
    private static Coordinates2D convertCord(Coordinates2D cord) {
        int newRow = Board.ROWS + 1 - (cord.row() + 1);
        int newCol = cord.col() + 1;
        return new Coordinates2D(newRow, newCol);
    }
}

