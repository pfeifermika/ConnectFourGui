package connectfour.gui;

import javax.swing.*;
import java.awt.*;

public class MenuBar extends JPanel {

    JButton btnNew;
    JButton btnSwitch;
    JButton btnQuit;


    MenuBar() {
        super();
        btnNew = new JButton("New");
        btnSwitch = new JButton("Switch");
        btnQuit = new JButton("Quit");

        add(btnNew);
        add(btnSwitch);
        add(btnQuit);

        btnQuit.addActionListener(e -> {
            ConnectFourWindow window
                    = (ConnectFourWindow) SwingUtilities.getWindowAncestor(MenuBar.this);
            window.quitButtonPressed();
        });

        btnNew.addActionListener(e -> {
            ConnectFourWindow window
                    = (ConnectFourWindow) SwingUtilities.getWindowAncestor(MenuBar.this);
            window.newButtonPressed();
        });

        btnSwitch.addActionListener(e -> {
            ConnectFourWindow window
                    = (ConnectFourWindow) SwingUtilities.getWindowAncestor(MenuBar.this);
            window.switchButtonPressed();
        });


    }


}
