package connectfour.model.ai;

import connectfour.model.GameState;
import connectfour.model.gametree.Node;

public class Minimax {

    public int calculateMove(GameState gameState) {
        Node root = new Node(gameState, gameState.getLevel());
        return root.getMove();
    }
}
