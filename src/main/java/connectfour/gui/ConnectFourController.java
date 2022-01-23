package connectfour.gui;

import connectfour.model.GameState;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConnectFourController implements ActionListener {

    private static ConnectFourController controller;

    ConnectFourWindow connectFourWindow;
    GameState gameState;

    private ConnectFourController(){}

    public static ConnectFourController getInstance() {
        if(controller == null){
            controller = new ConnectFourController();
        }
        return controller;
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
