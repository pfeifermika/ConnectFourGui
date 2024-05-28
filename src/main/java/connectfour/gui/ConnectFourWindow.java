package connectfour.gui;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Window containing a board and a buttons to interact with and play a game of
 * ConnectFour.
 */
public class ConnectFourWindow extends JFrame {

    /**
     * Constructs a new ConnectFourWindow and adds a menubar and a grid to it.
     */
    public ConnectFourWindow() {
        //standard constructor stuff
        super("ConnectFour");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Add MenuBar and Grid
        GameBoardPanel gameBoardPanel = new GameBoardPanel();
        getContentPane().add(BorderLayout.SOUTH, new MenuBar(gameBoardPanel));
        getContentPane().add(BorderLayout.CENTER, gameBoardPanel);

        //Add Listener for closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ConnectFourWindow.this.setVisible(false);
                ConnectFourWindow.this.dispose();
            }
        });
        pack();
    }
}