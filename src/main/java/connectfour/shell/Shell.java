package connectfour.shell;

import connectfour.model.Board;
import connectfour.model.Coordinates2D;
import connectfour.model.GameState;
import connectfour.model.Player;
import connectfour.model.exceptions.IllegalMoveException;
import connectfour.model.utility.ConnectFourUtility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;


/**
 * Utility class, managing user input and console output, to play a game of
 * connect four.
 * @deprecated
 * No longer used, could still work but no guarantees. Use {@link connectfour.Main} instead to start the programm.
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
     * Private constructor to ensure non-instantiability.
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
            char command;
            try {
                command = Character.toLowerCase(tokens[0].charAt(0));
            } catch (StringIndexOutOfBoundsException e) {
                command = ' ';
            }

            switch (command) {
                case 'n' -> newGame();
                case 'l' -> changeLevel(tokens);
                case 's' -> switchSides();
                case 'm' -> move(tokens);
                case 'w' -> printWitness();
                case 'p' -> System.out.println(game.toString());
                case 'h' -> printHelp();
                case 'q' -> {
                    run = false;
                }
                default -> errorMessage("Invalid command!");
            }
        }
    }

    /**
     * Starts a new game with the same difficulty and first player as the game
     * before.
     */
    private static void newGame() {
        game = new GameState();
        game.setLevel(level);
        if (firstPlayer == Player.MACHINE) {
            machineMove();
        }
    }


    /**
     * Changes the level to the entered level. If the input level is over 5
     * this will print an error message.
     *
     * @param tokens the user input.
     */
    private static void changeLevel(String[] tokens) {
        try {
            int newLevel = Integer.parseInt(tokens[1]);
            if (newLevel > 5 || newLevel < 1) {
                errorMessage("Level has to be between 1 and 5.");
            } else {
                game.setLevel(newLevel);
                level = newLevel;
            }
        } catch (NumberFormatException e) {
            errorMessage("Level has to be a number!");
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            errorMessage("Wrong command format.");
        } catch (IllegalArgumentException e) {
            errorMessage("Level has to be between 1 and 5.");
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
            firstPlayer = Player.MACHINE;
            machineMove();
        } else if (firstPlayer == Player.MACHINE) {
            firstPlayer = Player.HUMAN;
        }
    }


    /**
     * Tries to execute a human move.
     * <p>
     * If any of the steps, to make a move, fails prints an error message with
     * detailed information regarding the nature of the error.
     * <p>
     * Automatically checks if the game is over after executing the move.
     * If the game is not over executes a machine move, otherwise prints out a
     * winner message.
     *
     * @param tokens the user input.
     */
    private static void move(String[] tokens) {
        if (game.isGameOver()) {
            errorMessage("The game is over!");
            return;
        }

        Board newGamestate;

        try {
            int column = Integer.parseInt(tokens[1]);
            newGamestate = game.move(column - 1);
        } catch (NumberFormatException | NullPointerException
                | ArrayIndexOutOfBoundsException e) {
            errorMessage("Invalid Arguments!");
            return;
        } catch (IllegalArgumentException e) {
            errorMessage("The column is out of bounds");
            return;
        } catch (IllegalMoveException e) {
            errorMessage("Its not your turn");
            return;
        }

        if (newGamestate == null) {
            errorMessage("The column is already full!");
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
     * Tries to execute a machine move.
     * <p>
     * If any of the steps, to make a move, fails prints an error message with
     * detailed information regarding the nature of the error.
     * <p>
     * Automatically checks if the game is over after executing the move.
     * If the game is over prints out a winner message.
     */
    private static void machineMove() {
        if (game.isGameOver()) {
            errorMessage("The game is over!");
            return;
        }

        Board newGamestate;

        try {
            newGamestate = game.machineMove();
        } catch (IllegalMoveException e) {
            errorMessage("Its not the machines move!");
            return;
        } catch (InterruptedException e) {
            errorMessage("Error while calculating the machines move.");
            return;
        }

        if (newGamestate == null) {
            errorMessage("Error executing the calculated move.");
        } else if (newGamestate.isGameOver()) {
            game = newGamestate;
            printWinnerMessage();
        } else {
            game = newGamestate;
        }
    }


    /**
     * If the game is over prints a winner message, otherwise does nothing.
     */
    private static void printWinnerMessage() {
        if (game.isGameOver()) {
            Player player = game.getWinner();

            if (player == Player.HUMAN) {
                System.out.println("Congratulations! You won.");
            } else if (player == Player.MACHINE) {
                System.out.println("Sorry! Machine wins.");
            } else {
                System.out.println("Nobody wins. Tie.");
            }
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
                           c has to be an integer between 0 and"""
                + " " + Board.COLS + "\n" + """
                - witness: Prints out the coordinates of a winning group.
                           Only usable, when the game is won by a player.
                - print:   Prints the current board to the console.
                - help:    Prints out this help text.
                - quit:    Terminates the program.
                """);
    }

    /**
     * Prints out the coordinates of the winning connect four group
     * if the game has been won.
     */
    private static void printWitness() {
        if (game.getWinner() != null) {
            StringJoiner joiner = new StringJoiner(", ");
            Set<Coordinates2D> set = new TreeSet<>();
            game.getWitness().forEach(c ->
                    set.add(ConnectFourUtility.convertCord(c)));

            set.forEach(c ->
                    joiner.add(c.toString()));

            System.out.println(joiner);
        } else {
            errorMessage("The game is not won by any player!");
        }
    }

    private static void errorMessage(String message) {
        System.out.println("Error! " + message);
    }

}

