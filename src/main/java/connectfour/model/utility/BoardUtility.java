package connectfour.model.utility;

import connectfour.model.Coordinates2D;

import java.util.ArrayList;
import java.util.List;

import static connectfour.model.Board.ROWS;
import static connectfour.model.Board.COLS;

/**
 * Utility class.
 */
public final class BoardUtility {

    /**
     * Private constructor to indicate utility class.
     */
    private BoardUtility() {
        throw new AssertionError("Utility Class!");
    }

    /**
     * Calculates the starting coordinates needed to count the groups,
     * based on the layout of the board and stores them in an array.
     * <p>
     * The coordinates are ordered as follows:
     * <ul>
     *     <li>index 0...ROWS + COLS - 1:
     *          contains start coordinates for diagonal up</li>
     *     <li>index COLS - 1...ROWS + COLS - 1:
     *          contains start coordinates for horizontal.</li>
     *     <li>index COLS - 1...array.length - 1:
     *          contains start coordinates for diag down.</li>
     *     <li>index ROWS + COLS - 1...array.length - 1:
     *          contains start coordinates for vertical.</li>
     * </ul>
     *
     * @return array containing start coordinates to calculate groups
     */
    public static Coordinates2D[] calculateStartCoordinates() {
        int diagCount = ROWS + COLS - 1;

        List<Coordinates2D> startCoordinates = new ArrayList<>();

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
