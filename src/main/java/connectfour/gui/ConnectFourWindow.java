package connectfour.gui;

import connectfour.model.Board;
import connectfour.model.Coordinates2D;
import connectfour.model.GameState;
import connectfour.model.Player;
import connectfour.model.exceptions.IllegalMoveException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//implements observer
public class ConnectFourWindow extends JFrame {

    GameBoardPanel gameBoardPanel;

    private Board game;
    private int level;
    private Player firstPlayer = Player.HUMAN;
    boolean canMove;
    Thread machineMoveThread;
    private Board machineResult;

    ConnectFourWindow() {
        //standard constructor stuff
        super("ConnectFour");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //init model
        game = new GameState();
        gameBoardPanel = new GameBoardPanel();

        //Adding Content
        getContentPane().add(BorderLayout.SOUTH, new MenuBar());
        getContentPane().add(BorderLayout.CENTER, gameBoardPanel);


        //
        addListeners();
        //finalize
        canMove = true;
        setSize(500, 500);
    }

    private void addListeners() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ConnectFourWindow.this.setVisible(false);
                ConnectFourWindow.this.dispose();
            }
        });
    }

    protected void tokenClicked(Coordinates2D position) {
        if (game.isGameOver()) {
            displayErrorPopup("The game is over!");
            return;
        } else if (!canMove) {
            displayErrorPopup("The machine is still calculating its move."
                    + " Please wait...");
            return;
        }

        Board newGameState;

        try {
            newGameState = game.move(position.col() - 1);
        } catch (IllegalArgumentException e) {
            displayErrorPopup("There was an internal error, please try again!");
            return;
        } catch (IllegalMoveException e) {
            displayErrorPopup("Its not your turn");
            return;
        }

        if (newGameState == null) {
            displayErrorPopup("This column is already full!");
        } else if (newGameState.isGameOver()) {
            canMove = false;
            game = newGameState;
            updateTokens();
            displayWinnerPopup();
        } else {
            game = newGameState;
            updateTokens();
            machineMove();
        }

    }

    private void updateTokens(){
        for (int i = 0; i < Board.ROWS; i++) {
            for (int j = 0; j < Board.COLS; j++) {


            }
        }
    }

    private void machineMove() {
        machineMoveThread = new Thread(() -> {
            canMove = false;
            try {
                machineResult = game.machineMove();
            } catch (IllegalMoveException e) {
                displayErrorPopup("Its not the machines move!");
            } catch (InterruptedException e) {
                displayErrorPopup("Error while calculating the machines move.");
            }
            canMove = true;
        });
        machineMoveThread.start();
    }


    protected void levelChanged(int newLevel) {
        System.out.println("level changed");
    }

    protected void quitButtonPressed() {
        System.out.println("quit");
        System.exit(1);
    }

    protected void switchButtonPressed() {
        System.out.println("switch");
        machineMoveThread.interrupt();
    }

    protected void newButtonPressed() {
        System.out.println("new");
        machineMoveThread.interrupt();
        game = new GameState();
        game.setLevel(level);
        if (firstPlayer == Player.MACHINE) {
            machineMove();
        }
        canMove = true;
    }

    private void displayErrorPopup(String message) {
        JOptionPane.showMessageDialog(this, message, "Error!", JOptionPane.ERROR_MESSAGE);
    }

    private void displayWinnerPopup() {
        JOptionPane.showMessageDialog(this, "WINNER", "WINNER!", JOptionPane.INFORMATION_MESSAGE);
    }


}
