package connectfour.gui;

import connectfour.model.Coordinates2D;
import connectfour.model.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Token extends JPanel {

    /**
     * Tuning parameters to change the size of the tokens and witness size.
     */
    private static final double TOKEN_SIZE = 0.9;
    private static final double WITNESS_SIZE = 0.4;
    /**
     * Position of this Token on the grid.
     */
    private final Coordinates2D position;
    private Player player;
    private boolean isWitness;

    Token(Coordinates2D pPosition) {
        position = pPosition;
        player = Player.TIE;
        isWitness = false;
        setBackground(Color.BLUE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ConnectFourWindow window
                        = (ConnectFourWindow) SwingUtilities.getWindowAncestor(Token.this);
                window.tokenClicked(position);
            }
        });

    }

    public void updatePlayer(Player player){
        this.player = player;
    }

    public void changeWitness(){
        this.isWitness = !this.isWitness;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        //activate antialiasing for cleaner circles edges
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int height = g.getClipBounds().height;
        int width = g.getClipBounds().width;
        int radius = Math.min(height, (int) (height * TOKEN_SIZE)) / 2;

        /*
        //paint the background
        g2.setColor(Color.BLUE);
        g2.fillRect(0,0,width,height);

         */

        //select tokens color
        switch (player) {
            case HUMAN -> g2.setColor(Color.YELLOW);
            case MACHINE -> g2.setColor(Color.RED);
            case TIE -> g2.setColor(Color.WHITE);
        }
        //draw token
        g2.fillOval((width / 2) - radius,
                (height / 2) - radius,
                radius * 2,
                radius * 2);


        //draw black dot to show witnesses
        if (isWitness) {
            radius = Math.min(height, (int) (height * WITNESS_SIZE));
            g2.fillOval((width / 2) - radius,
                    (height / 2) - radius,
                    radius * 2,
                    radius * 2);
        }
    }





}
