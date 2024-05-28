package connectfour.gui;

import connectfour.model.GameState;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import java.util.stream.IntStream;

/**
 * Panel containing buttons to interact with a game of connect four.
 */
public class MenuBar extends JPanel {

    /**
     * The {@code GameBoardPanel} reference to handle button input.
     */
    private final GameBoardPanel gameBoardPanel;
    /**
     * Button to start a new game.
     */
    private final JButton btnNew;
    /**
     * Button to switch sides and start a new game.
     */
    private final JButton btnSwitch;
    /**
     * Button to quit the program.
     */
    private final JButton btnQuit;
    /**
     * Dropdown menu to set the current difficulty.
     * Ranging from 1 to {@link GameState#MAX_LEVEL} inclusive.
     */
    private final JComboBox<Integer> dropDownLevels;

    /**
     * Constructs a new MenuBar containing three buttons and the dropdown menu.
     *
     * @param pGameBoardPanel the {@code GameBoardPanel} reference.
     */
    public MenuBar(GameBoardPanel pGameBoardPanel) {
        super();
        gameBoardPanel = pGameBoardPanel;
        btnNew = new JButton("New");
        btnSwitch = new JButton("Switch");
        btnQuit = new JButton("Quit");

        Integer[] levels = IntStream.rangeClosed(1, GameState.MAX_LEVEL)
                .boxed().toArray(Integer[]::new);
        dropDownLevels = new JComboBox<>(levels);
        dropDownLevels.setSelectedItem(levels[3]);

        add(dropDownLevels);
        add(btnNew);
        add(btnSwitch);
        add(btnQuit);

        addListeners();
    }

    /**
     * Adds a listener to each button to handle input accordingly.
     */
    private void addListeners() {
        dropDownLevels.addActionListener(e -> {
            gameBoardPanel.levelChanged(dropDownLevels.getItemAt(
                    dropDownLevels.getSelectedIndex()));
        });

        btnQuit.addActionListener(e -> {
            gameBoardPanel.quitButtonPressed();
        });

        btnNew.addActionListener(e -> {
            gameBoardPanel.newButtonPressed();
        });

        btnSwitch.addActionListener(e -> {
            gameBoardPanel.switchButtonPressed();
        });
    }

}