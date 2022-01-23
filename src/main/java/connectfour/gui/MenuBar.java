package connectfour.gui;

import javax.swing.*;

public class MenuBar extends JPanel {

    JButton btnNew;
    JButton btnSwitch;
    JButton btnQuit;


    MenuBar(){
        super();
        btnNew = new JButton("New");
        btnSwitch = new JButton("Switch");
        btnQuit = new JButton("Quit");

        add(btnNew);
        add(btnSwitch);
        add(btnQuit);
    }




}
