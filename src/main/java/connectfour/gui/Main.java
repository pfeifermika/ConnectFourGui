package connectfour.gui;

public class Main {

    public static void main(String[] args) {
        // schedule a job for the event-dispatching thread:
        // creating and showing this application’s GUI
        javax.swing.SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        ConnectFourWindow connectFourWindow = new ConnectFourWindow();
        connectFourWindow.setVisible(true);
    }


}
