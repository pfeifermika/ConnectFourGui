package connectfour.gui;

import connectfour.model.Coordinates2D;
import connectfour.model.Player;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Visual representation of a player token in a game of Connect Four.
 * <p>
 * Draws itself according to its internal state, wich can be updated by using
 * the {@link Token#updateToken(Player, boolean)} method.
 * <p>
 * Provides tuning parameters to change the size of the token and the witness
 * dot.
 */
public class Token extends JPanel {

    /**
     * Tuning parameter to change the size of the tokens
     */
    private static final double TOKEN_SIZE = 0.9;
    /**
     * Tuning parameter to change the size of the witness dot
     */
    private static final double WITNESS_SIZE = 0.2;
    /**
     * Preferred size of each token
     */
    private static final Dimension PREFERRED_SIZE
            = new Dimension(100, 100);
    /**
     * Position of this Token on the grid.
     */
    private final Coordinates2D position;
    /**
     * Displayed player.
     */
    private Player player;
    /**
     * If this token is a witness
     */
    private boolean isWitness;

    /**
     * Constructs a new Token with the given position.
     * <p>
     * Also sets default values and paints the background blue.
     *
     * @param pPosition the position of this token on the grid.
     */
    protected Token(Coordinates2D pPosition) {
        position = pPosition;
        player = Player.TIE;
        isWitness = false;
        setBackground(Color.BLUE);
        setPreferredSize(PREFERRED_SIZE);
    }

    /**
     * Compares the new values with the current ones and updates them,
     * if necessary. If any values changed, repaints itself to correctly
     * represent the internal state.
     *
     * @param newPlayer  the new player represented by this token
     * @param newWitness if this token is a witness
     */
    protected void updateToken(Player newPlayer, boolean newWitness) {
        if (this.player != newPlayer || isWitness != newWitness) {
            isWitness = newWitness;
            player = newPlayer;
            repaint();
        }
    }

    /**
     * Paints this component according to its internal state.
     * <p>
     * Draws a circle in the respective color to the {@link Token#player}. If
     * {@link Token#isWitness} is true also draws a black circle on top of the
     * player circle.
     *
     * @param g the graphics
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        //activate antialiasing for cleaner circle edges
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int height = g.getClipBounds().height;
        int width = g.getClipBounds().width;
        int radiusHeight = Math.min(height, (int) (height * TOKEN_SIZE)) / 2;
        int radiusWidth = Math.min(width, (int) (width * TOKEN_SIZE)) / 2;
        //minimum value of the two has to be used, to ensure correct size
        int radius = Math.min(radiusHeight, radiusWidth);

        //select tokens color
        switch (player) {
            case HUMAN -> g2.setColor(Color.YELLOW);
            case MACHINE -> g2.setColor(Color.RED);
            default -> g2.setColor(Color.WHITE);
        }
        //draw token
        g2.fillOval((width / 2) - radius,
                (height / 2) - radius,
                radius * 2,
                radius * 2);

        //draw black dot to show witnesses
        if (isWitness) {
            g2.setColor(Color.BLACK);
            radius = Math.min(height, (int) (height * WITNESS_SIZE));
            g2.fillOval((width / 2) - radius,
                    (height / 2) - radius,
                    radius * 2,
                    radius * 2);
        }
    }

    /**
     * @return the position of this token on the grid.
     */
    protected Coordinates2D getPosition() {
        return position;
    }

}