package connectfour.model.utility;

import connectfour.model.Board;
import connectfour.model.Coordinates2D;

import java.util.ArrayList;
import java.util.List;

import static connectfour.model.Board.ROWS;
import static connectfour.model.Board.COLS;

/**
 * Utility class.
 */
public final class ConnectFourUtility {

    /**
     * Private constructor to indicate utility class.
     */
    private ConnectFourUtility() {
        throw new AssertionError("Utility Class!");
    }

    /**
     * Calculates the starting coordinates needed to count the groups,
     * based on the layout of the board and stores them in an array.
     * <p>
     * The coordinates are ordered as follows:
     * <ul>
     *     <li>index 0...ROWS + COLS - 1:
     *          contains start coordinates for left down to right up
     *          diagonal calculation</li>
     *     <li>index COLS - 1...ROWS + COLS - 1:
     *          contains start coordinates for horizontal calculation.</li>
     *     <li>index COLS - 1...array.length - 1:
     *          contains start coordinates for left up to right down
     *          diagonal calculation.</li>
     *     <li>index ROWS + COLS - 1...array.length - 1:
     *          contains start coordinates for vertical calculation.</li>
     * </ul>
     *
     * @return array containing all starting coordinates to calculate groups.
     */
    public static Coordinates2D[] calculateAllStartCoordinates() {
        int diagCount = ROWS + COLS - 1;

        List<Coordinates2D> startCoordinates = new ArrayList<>();

        // formula discussed in class
        for (int diag = 0; diag < diagCount; diag++) {
            int row = Math.max(ROWS - 1 - diag, 0);
            int col = Math.max(diag - ROWS + 1, 0);
            startCoordinates.add(new Coordinates2D(row, col));
        }

        // adding top row for vertical down and diagonal down
        for (int i = 1; i < COLS; i++) {
            startCoordinates.add(0, new Coordinates2D(ROWS - 1, i));
        }
        return startCoordinates.toArray(Coordinates2D[]::new);
    }

    /**
     * Converts a {@link Coordinates2D} of a board where (0,0) is in the top
     * left corner to a {@link Coordinates2D} of a board, where (1,1) is in the
     * bottom left corner. Basically just moving the origin of the
     * coordinate system.
     *
     * @param cord the coordinate to be converted.
     * @return the converted coordinate.
     */
    public static Coordinates2D convertCord(Coordinates2D cord) {
        int newRow = Board.ROWS + 1 - (cord.row() + 1);
        int newCol = cord.col() + 1;
        return new Coordinates2D(newRow, newCol);
    }

}
