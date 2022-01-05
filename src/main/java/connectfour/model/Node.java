package connectfour.model;


/**
 * Models a Node in a 7-ary game tree.
 * Implements methods to set children of this node and a method to get
 * the best possible move of the subtree
 * of this node using the minmax algorithm.
 */
public class Node {

    /**
     * Represents the value of this node.
     */
    private int evaluation;

    /**
     * The height of the node in the game tree.
     */
    private final int height;

    /**
     * Contains all the possible children of this node.
     */
    private final Node[] children = new Node[Board.COLS];

    /**
     * Creates a new Node with given height and evaluation value.
     * The evaluation value is initially set to the evaluation value of the
     * corresponding GameState.
     *
     * @param height     the height of the node inside the game tree.
     * @param evaluation the evaluation of this node.
     */
    public Node(int height, int evaluation) {
        this.height = height;
        this.evaluation = evaluation;
    }

    /**
     * Sets a child of this node at a given index.
     *
     * @param child the child to be set.
     * @param index the index where the child will be set.
     */
    public void setChild(Node child, int index) {
        if (index > children.length || index < 0) {
            throw new IllegalArgumentException("Index out of bounds");
        }
        this.children[index] = child;
    }

    /**
     * First sets the evaluation of all nodes in the Tree.
     * Then returns the index of the leftmost children
     * with the highest evaluation.
     * This index corresponds to the column of the best possible move.
     *
     * @return the index of the leftmost children with the highest evaluation
     */
    public int getBestMove() {
        setEvaluations(true);
        return this.getIndexOfMaxValue();
    }

    /**
     * Post order tree traversal starting at this node.
     * Updates the evaluation of all inner nodes in the subtree,
     * including this node.
     * <p>
     * If a node is a leaf its evaluation value is not changed.
     * For inner nodes, the value is set to the sum of their own evaluation and
     * the extremum of the child nodes.
     * <p>
     * The extremum is chosen depending on the height of the node and therefore
     * depends on the player to move. This is an implementation of the
     * minimax algorithm to maximise the evaluation for the player whose turn it
     * is in the root node.
     *
     * @param max when true the max child value is used,
     *            when false the min child value.
     */
    private void setEvaluations(boolean max) {
        if (isLeaf()) {
            return;
        }

        for (Node n : children) {
            if (n != null) {
                n.setEvaluations(!max);
            }
        }
        if (max) {
            evaluation = getMaxValueOfChildren() + evaluation;
        } else {
            evaluation = getMinValueOfChildren() + evaluation;
        }
    }

    /**
     * Returns the index of the maximum value between child nodes.
     * If multiple values are equal returns the leftmost.
     *
     * @return the index of the maximum value between all child nodes.
     */
    private int getIndexOfMaxValue() {
        int maxValue = getMaxValueOfChildren();
        int index = -1;
        for (int i = 0; i < children.length; i++) {
            if (children[i] != null && maxValue == children[i].evaluation) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Iterates over all child nodes and returns the maximum value.
     *
     * @return the maximum evaluation value between all children.
     */
    private int getMaxValueOfChildren() {
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
     * Iterates over all child nodes and returns the minimum value.
     *
     * @return the minimum evaluation value between all children.
     */
    private int getMinValueOfChildren() {
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

    /**
     * Checks if a node is a leaf.
     *
     * @return true if the node is a leaf, false otherwise.
     */
    private boolean isLeaf() {
        return height == 0;
    }
}
