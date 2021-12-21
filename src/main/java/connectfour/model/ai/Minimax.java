package connectfour.model.ai;

import connectfour.model.GameState;
import connectfour.model.Node;

public class Minimax {

    public int calculateMove(GameState gameState, int depth) {
        Node root = new Node(gameState, depth);
        return root.getMove();
    }
}
