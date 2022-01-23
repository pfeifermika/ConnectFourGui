package connectfour.gui;

import connectfour.model.Board;
import connectfour.model.Coordinates2D;

import javax.swing.*;
import java.awt.*;

public class GameBoardPanel extends JPanel {



    GameBoardPanel(){
        super();
        setLayout(new GridLayout(Board.ROWS,Board.COLS));
        addTokensToBoard();
    }

    private void addTokensToBoard(){
        for (int i = 0; i < Board.ROWS; i++) {
            for (int j = 0; j < Board.COLS; j++) {
                add(new Token(new Coordinates2D(i,j)));
            }
        }
    }

}
