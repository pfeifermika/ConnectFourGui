package connectfour.model.gametree;

import connectfour.model.Board;
import connectfour.model.GameState;
import connectfour.model.Player;


/**
 * Modeling a Node in a 7-ary game tree.
 * Creating a root node automatically creates the full subtree
 * of every possible move, up to a given depth.
 * Implements methods to set the evaluation of every child node
 * and a way to get the column of the best possible move in a subtree
 * using the minmax algorithm.
 */
public class Node {


    private final GameState gameState;
    private int evaluation;
    private final int depth;
    private final Node[] children = new Node[Board.COLS];


    /**
     * Creates a root node and its subtree containing all possible moves, up to
     * a given depth.
     *
     * @param gameState the gameState of the current game
     * @param depth     the depth of the resulting tree
     */
    public Node(GameState gameState, int depth) {
        this.depth = depth;
        this.gameState = gameState;
        createChildren();
    }

    /**
     * Recursively creates a complete subtree up to a given depth,
     * containing all possible moves
     */
    private void createChildren() {
        if (depth == 0) {
            return;
        }

        for (int i = 0; i < Board.COLS; i++) {
            GameState gameState = (GameState) this.gameState.clone();

            if (gameState.insertChip(i, gameState.getFirstPlayer())) {
                gameState.setFirstPlayer(Player.getNextPlayer(gameState.getFirstPlayer()));
                children[i] = new Node(gameState, depth - 1);
            }
        }
    }

    /**
     * First sets the evaluation of all nodes in the Tree.
     * Then returns the index of the leftmost children
     * with the highest evaluation.
     * This index corresponds to the column of the best possible move.
     *
     * @return the index of the leftmost children with the highest evaluation
     */
    public int getMove() {
        setEvaluations();
        return this.getMaxIndex();
    }

    /**
     * Post order tree traversal starting at the root node.
     * Sets the evaluation value of all nodes in the tree.
     * If a node is a leaf its evaluation value is equivalent to
     * {@code GameState.evaluateBoard}.
     * For inner nodes, the value is the sum of their own board evaluation and
     * the extremum of the child nodes.
     */
    private void setEvaluations() {
        if (isLeaf()) {
            evaluation = gameState.evaluateBoard();
            return;
        }
        for (Node n : children) {
            if (n != null) {
                n.setEvaluations();
            }
        }
        if (depth % 2 != 0) {
            evaluation = getMinChildren() + gameState.evaluateBoard();
        } else {
            evaluation = getMaxChildren() + gameState.evaluateBoard();
        }
    }


    /**
     * Returns the index of the maximum value between child nodes.
     * If multiple values are equal returns the leftmost.
     *
     * @return the index of the maximum value between all child nodes.
     */
    private int getMaxIndex() {
        int maxIndex = 0;
        for (int i = 1; i < children.length; i++) {
            if (children[i] != null && children[i].evaluation > children[maxIndex].evaluation) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    /**
     * Goes through all child nodes and gets maximum value.
     * If multiple values are equal returns the leftmost.
     *
     * @return the largest evaluation value between all children
     */
    private int getMaxChildren() {
        if (isLeaf()) {
            return 0;
        }
        int maxValue = Integer.MIN_VALUE;
        for (Node child : children) {
            if (child != null && child.evaluation > maxValue) {
                maxValue = child.evaluation;
            }
        }
        return maxValue;
    }


    /**
     * Goes through all child nodes and gets minimum value.
     * If multiple values are equal returns the leftmost.
     *
     * @return the smallest evaluation value between all children
     */
    private int getMinChildren() {
        if (isLeaf()) {
            return 0;
        }
        int minValue = Integer.MAX_VALUE;
        for (Node child : children) {
            if (child != null && child.evaluation < minValue) {
                minValue = child.evaluation;
            }
        }
        return minValue;
    }

    private boolean isLeaf() {
        return depth == 0;
    }
}
