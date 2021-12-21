package connectfour.shell;

import connectfour.model.Board;
import connectfour.model.GameState;
import connectfour.model.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Utility class, managing user input and console output.
 */
public class Shell {

    private static Board game;
    private static boolean gameWon;

    private Shell() {
    }

    public static void main(String[] args) throws IOException {
        BufferedReader stdin =
                new BufferedReader(new InputStreamReader(System.in));
        execute(stdin);

        // GameBoard board = new GameBoard();
        // board.insertChip(0,Player.HUMAN);
        // board.insertChip(1,Player.HUMAN);
        // board.insertChip(3,Player.HUMAN);

        // board.insertChip(3,Player.COMPUTER);
        // board.insertChip(3,Player.COMPUTER);
        // board.insertChip(3,Player.COMPUTER);


        // System.out.println(board.evaluateBoard());


    }


    private static void execute(BufferedReader stdin) throws IOException {
        game = new GameState();
        boolean run = true;
        boolean humanMoved = false;
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
                case 's' -> {
                    switchPlayer();
                    humanMoved = true;
                }
                case 'm' -> {
                    humanMoved = humanMove(tokens);
                }
                case 'w' -> witness();
                case 'p' -> print();
                case 'h' -> help();
                case 'q' -> run = false;
                default -> System.out.println("Error! Invalid command!");
            }

            if (humanMoved) {
                machineMove();
                printWinnerMessage();
            }
            humanMoved = false;
        }
    }


    private static boolean humanMove(String[] tokens) {
        if (gameWon) {
            return false;
        }
        int column;

        try {
            column = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException e) {
            System.out.println("Error! Invalid Arguments!");
            return false;
        }

        Board board = game.move(column - 1);
        if (board == null) {
            System.out.println("Error! An error has occurred while making your move. Please try again.");
            return false;
        } else {
            game = board;
            return true;
        }
    }

    private static boolean machineMove() {
        if (gameWon) {
            return false;
        }

        Board board = game.machineMove();
        if (board == null) {
            System.out.println("Error! An error has occurred while making the computer move.");
            return false;
        } else {
            game = board;
            return true;
        }
    }


    private static void switchPlayer() {
        gameWon = false;
        game = new GameState();
        game.setLevel(1);
    }

    private static void witness() {
        //TODO
    }


    private static void changeLevel(String[] tokens) {
        try {
            int level = Integer.parseInt(tokens[1]);
            game.setLevel(level);
        } catch (NumberFormatException e) {
            System.out.println("Error! Invalid Arguments!");
        }
    }

    private static void printWinnerMessage() {
        Player player = game.getWinner();

        if (player == Player.HUMAN) {
            System.out.println("Congratulations! You won.");
            gameWon = true;
        } else if (player == Player.COMPUTER) {
            System.out.println("Sorry! Machine wins");
            gameWon = true;
        }
    }


    private static void print() {
        System.out.println(game.toString());
    }

    private static void help() {
        System.out.print("""
                HELP MENU:
                                
                - 'new': starts a new game
                - 'level -l': sets the difficulty to 'l' (default is 4)
                - 'switch':
                - '':
                - '':
                - '':- '':
                - '':
                - '':
                                
                                
                                
                                
                                
                                
                """); //TODO
    }

    private static void newGame() {
        gameWon = false;
        game = new GameState();
    }


}

