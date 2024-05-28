package connectfour;

import connectfour.gui.ConnectFourWindow;

/**
 * Utility class to start the program.
 */
public final class Main {

    /**
     * Private constructor to ensure non-instantiability.
     */
    private Main() {
        throw new AssertionError("Utility Class!");
    }

    /**
     * Schedules the creation of the gui for the event-dispatching thread.
     *
     * @param args the program arguments
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    /**
     * Creates a new window and sets it to visible.
     */
    private static void createAndShowGUI() {
        ConnectFourWindow connectFourWindow = new ConnectFourWindow();
        connectFourWindow.setVisible(true);
    }
}