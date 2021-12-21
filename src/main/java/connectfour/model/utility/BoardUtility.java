package connectfour.model.utility;

import connectfour.model.Coordinates2D;

import java.util.ArrayList;

import static connectfour.model.Board.ROWS;
import static connectfour.model.Board.COLS;

public class BoardUtility {

    private BoardUtility(){

    }

    //TODO CLEANUP types
    //diagCount = ROWS + COLS - 1
    //index 0...diagCount für diag up
    //index COLS - 1 ... diagCount für horizontal rechts
    //index COLS - 1 ... array.length für diag runter
    //index diagCount ... array.length für runter
    public static Coordinates2D[] calculateStartCoordinates() {
        int diagCount = ROWS + COLS - 1;

        ArrayList<Coordinates2D> startCoordinates = new ArrayList<>();

        for (int diag = 0; diag < diagCount; diag++) {
            int row = Math.max(ROWS - 1 - diag, 0);
            int col = Math.max(diag - ROWS + 1, 0);
            startCoordinates.add(new Coordinates2D(row, col));
        }
        for (int i = 1; i < COLS; i++) {
            startCoordinates.add(0, new Coordinates2D(ROWS - 1, i));
        }
        return startCoordinates.toArray(Coordinates2D[]::new);
    }

}
