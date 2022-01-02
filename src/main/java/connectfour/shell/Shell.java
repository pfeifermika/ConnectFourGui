package connectfour.shell;

import connectfour.model.Board;
import connectfour.model.Coordinates2D;
import connectfour.model.GameState;
import connectfour.model.Player;
import connectfour.model.exceptions.IllegalMoveException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


/**
 * Utility class, managing user input and console output.
 */
public class Shell {

    private static Board game;
    private static Player startPlayer = Player.HUMAN;
    private static int level = 4;


    private Shell() {
    }

    public static void main(String[] args) throws IOException {
        BufferedReader stdin =
                new BufferedReader(new InputStreamReader(System.in));
        execute(stdin);
      //TreeSet<Coordinates2D> list = new TreeSet<>();
      //list.add(new Coordinates2D(2,2));
      //list.add(new Coordinates2D(4,2));
      //list.add(new Coordinates2D(5,5));
      //list.add(new Coordinates2D(4,5));
      //list.add(new Coordinates2D(5,5));
      //list.add(new Coordinates2D(1,2));

      //System.out.println(list);
    }


    private static void execute(BufferedReader stdin) throws IOException {
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
                case 'p' -> print();
                case 'h' -> printHelp();
                case 'q' -> run = false;
                default -> System.out.println("Error! Invalid command!");
            }
        }


    }

    private static void newGame() {
        game = new GameState();
        game.setLevel(level);
        if (startPlayer == Player.COMPUTER) {
            machineMove();
        }
    }

    private static void changeLevel(String[] tokens) {
        try {
            int newLevel = Integer.parseInt(tokens[1]);
            game.setLevel(newLevel);
            level = newLevel;
        } catch (NumberFormatException | NullPointerException e) {
            System.out.println("Error! Invalid Arguments!");
        }
    }

    private static void switchSides() {
        game = new GameState();
        game.setLevel(level);
        if (startPlayer == Player.HUMAN) {
            startPlayer = Player.COMPUTER;
            machineMove();
        } else if (startPlayer == Player.COMPUTER) {
            startPlayer = Player.HUMAN;
        }
    }

    private static void move(String[] tokens) {
        if (game.isGameOver()) {
            System.out.println("Error! The game is over! Please start a new one");
            return;
        }

        Board newGamestate;

        try {
            int column = Integer.parseInt(tokens[1]);
            newGamestate = game.move(column - 1);
        } catch (NumberFormatException | NullPointerException | ArrayIndexOutOfBoundsException e) {
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
            System.out.println("Error! While making your move an error has occurred please try again!");
        } else if (newGamestate.isGameOver()) {
            game = newGamestate;
            printWinnerMessage();
        } else {
            // game is not finished.
            game = newGamestate;
            machineMove();
        }

    }

    private static void machineMove() {
        if (game.isGameOver()) {
            System.out.println("Error! The game is over! Please start a new one");
            return;
        }

        Board newGamestate;

        try {
            newGamestate = game.machineMove();
        } catch (IllegalMoveException e) {
            System.out.println("Something went wrong when calculating the computer move");
            return;
        }

        if (newGamestate == null) {
            System.out.println("Something went wrong when calculating the computer move 2.0");
        } else if (newGamestate.isGameOver()) {
            game = newGamestate;
            printWinnerMessage();
        } else {
            game = newGamestate;
        }
    }

    private static void printWinnerMessage() {
        Player player = game.getWinner();

        if (player == Player.HUMAN) {
            System.out.println("Congratulations! You won.");
        } else if (player == Player.COMPUTER) {
            System.out.println("Sorry! Machine wins");
        } else {
            System.out.println("Nobody wins. Tie.");
        }
    }


    private static void print() {
        System.out.println(game.toString());
    }

    private static void printHelp() {
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

    private static Coordinates2D convertCord(Coordinates2D cord){
        /*
        int ROWS = 6;
        int COLS = 7;
        1 -> 6
        2 -> 5
        3 -> 4
        4 -> 3
        5 -> 2
        6 -> 1

        row++;
        row = -row
        row = ROWS + 1 + row;

        row = ROWS + 2 - row;

        col = col + 1

         */

        int newRow = Board.ROWS + 1 - (cord.row() + 1);
        int newCol = cord.col() + 1;
        return new Coordinates2D(newRow,newCol);
    }


}

