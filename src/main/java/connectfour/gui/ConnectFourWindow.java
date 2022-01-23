package connectfour.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ConnectFourWindow extends JFrame {




    ConnectFourWindow(){
        //standard constructor stuff
        super("ConnectFour");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //-----

        //Adding Content
        getContentPane().add(BorderLayout.SOUTH,new MenuBar());
        getContentPane().add(BorderLayout.CENTER,new GameBoardPanel());


        //
        addListeners();
        //finalize
        setSize(500,500);
    }

    private void createMenuBar(){

    }


    private void addListeners(){
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ConnectFourWindow.this.setVisible(false);
                ConnectFourWindow.this.dispose();
            }
        });
    }





}
