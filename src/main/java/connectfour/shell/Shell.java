package connectfour.shell;

import connectfour.model.Board;
import connectfour.model.GameBoard;
import connectfour.model.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;


/**
 * Utility class, managing user input and console output.
 */
public class Shell {

    private static Board game;

    private Shell() {} // utility class implies private constructor

    public static void main(String[] args) throws IOException {
        BufferedReader stdin =
                new BufferedReader(new InputStreamReader(System.in));
        test();
        //execute(stdin);
    }

    private static void test(){
        GameBoard gameBoard = new GameBoard();
        gameBoard.insertChip(0,Player.HUMAN);
        gameBoard.insertChip(1,Player.HUMAN);
        gameBoard.insertChip(2,Player.HUMAN);
        gameBoard.insertChip(3,Player.HUMAN);
        gameBoard.insertChip(5,Player.HUMAN);
        gameBoard.insertChip(6,Player.HUMAN);
        gameBoard.insertChip(2,Player.HUMAN);
        gameBoard.insertChip(3,Player.HUMAN);
        gameBoard.insertChip(1,Player.HUMAN);
        gameBoard.insertChip(2,Player.HUMAN);
        gameBoard.insertChip(3,Player.HUMAN);
        gameBoard.insertChip(4,Player.HUMAN);
        System.out.println(Arrays.toString(gameBoard.countHorizontalGroups(Player.HUMAN)));
        System.out.println(Arrays.toString(gameBoard.countVerticalGroups(Player.HUMAN)));
        System.out.println(gameBoard.toString());
    }


    private static void execute(BufferedReader stdin) throws IOException {
        game = new GameBoard();
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
                case 's' -> switchPlayer();
                case 'm' -> humanMove(tokens);
                case 'w' -> witness();
                case 'p' -> print();
                case 'h' -> help();
                case 'q' -> run = false;
                default -> System.out.println("Error! Invalid command!");
            }
        }
    }

    private static void newGame() {
        game = new GameBoard();
    }

    private static void changeLevel(String[] tokens) {
        try {
            int level = Integer.parseInt(tokens[1]);
            game.setLevel(level);
        } catch (NumberFormatException e) {
            System.out.println("Error! Invalid Arguments!");
        }
    }

    private static void humanMove(String[] tokens) {
        int column;
        try {
            column = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException e) {
            System.out.println("Error! Invalid Arguments!");
            return;
        }
        Board board = game.move(column);
        if(board == null){
            System.out.println("Error! An error has occurred while making your move. Please try again.");
        }else {
            game = board;
            machineMove();
        }
    }

    private static void machineMove() {
        Board board = game.machineMove();
        if(board == null){
            System.out.println("Error! An error has occurred while making the computer move.");
        }else {
            game = board;
        }
    }

    private static void switchPlayer() {
        //TODO
    }

    private static void witness() {
        //TODO
    }

    private static void print() {
        System.out.println(game.toString());
    }

    private static void help() {
        System.out.println("HELPTEXT HERE"); //TODO
    }







}
