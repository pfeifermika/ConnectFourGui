package connectfour.gui;

import connectfour.model.Board;
import connectfour.model.Coordinates2D;
import connectfour.model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameBoardPanel extends JPanel {

    List<Token> tokens = new ArrayList<>();

    GameBoardPanel() {
        super();
        setLayout(new GridLayout(Board.ROWS, Board.COLS));
        addTokensToBoard();
    }

    protected void updateToken(Coordinates2D position, Player player, boolean witness) {
        int index = position.row() * Board.ROWS + position.col();
        Token token = tokens.get(index);
        token.updatePlayer(player);
        if (witness) {
            token.changeWitness();
        }
        token.repaint();
    }

    private void addTokensToBoard() {
        for (int i = 0; i < Board.ROWS; i++) {
            for (int j = 0; j < Board.COLS; j++) {
                Token token = new Token(new Coordinates2D(i, j));
                add(token);
                token.add(token);

            }
        }
    }

}
