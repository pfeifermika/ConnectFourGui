package connectfour.model.gametree;

import connectfour.model.Board;
import connectfour.model.GameBoard;

public class Node {

    /*
    - Im obersten Knoten wird geschaut, wo ist der max wert und in den index wird der stein geworfen
    - In einem Blatt Knoten wird einfach die Evaluation gespeichert.
    - In einem inneren Knoten wird das Extremum der kinder addiert auf die evaluation vom knoten selbst
                    -minimum für depth.isEven
                    -maximum für depth.isUneven

    -Spalte des Computers ist maximaler Wert der kinder der wurzel


     methoden für getMin und getMax der Children

     */

    public Node parent;
    public GameBoard gameBoard;
    int evaluation;
    int depth;
    Node[] children;


    public Node(Node parent, GameBoard gameBoard, int depth) {
        this.depth = depth;
        this.parent = parent;
        this.gameBoard = gameBoard;
        //evaluation = gameBoard.evaluateBoard();
    }

    public int getMove() {
        this.setEvaluations();
        return this.getMaxIndex();
    }

    private boolean isLeaf() {
        return this.children == null;
    }

    //post order Tree traversal
    private void setEvaluations() {
        if (isLeaf()) {
            //this.evaluation = this.gameBoard.evaluateBoard();
            return;
        }
        for (Node n : children
        ) {
            n.setEvaluations();
        }
        if (this.depth % 2 == 0) {
            this.evaluation = getMinChildren() + this.evaluation;
        } else {
            this.evaluation = getMaxChildren() + this.evaluation;
        }
    }


    private int getMaxIndex() {
        int maxIndex = 0;
        for (int i = 1; i < children.length; i++) {
            if (children[i].evaluation > children[maxIndex].evaluation) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    //can only be used on non leaf nodes
    private int getMaxChildren() {
        if (isLeaf()) {
            return -1;
        }
        int maxValue = Integer.MIN_VALUE;
        for (int i = 0; i < children.length; i++) {
            if (children[i].evaluation > maxValue) {
                maxValue = children[i].evaluation;
            }
        }
        return maxValue;
    }

    //can only be used on non leaf nodes
    private int getMinChildren() {
        if (isLeaf()) {
            return -1;
        }
        int minValue = Integer.MAX_VALUE;
        for (int i = 0; i < children.length; i++) {
            if (children[i].evaluation < minValue) {
                minValue = children[i].evaluation;
            }
        }
        return minValue;
    }


}
