package connectfour.model;

/**
 * Two-dimensional coordinate.
 *
 * @param row the row
 * @param col the col
 */
public record Coordinates2D(int row, int col)
        implements Comparable<Coordinates2D> {

    /**
     * Returns a string representation of this coordinate in the
     * following format:
     * <p>(row,col)</p>
     *
     * @return the string representation of this coordinate.
     */
    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }

    /**
     * Compares this coordinate with the given one.
     * First compares the row then the col properties.
     *
     * @param o the coordinate to be compared
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(Coordinates2D o) {
        if (this.row < o.row) {
            return -1;
        } else if (this.row > o.row) {
            return 1;
        }

        if (this.col < o.col) {
            return -1;
        } else if (this.col > o.col) {
            return 1;
        }
        return 0;
    }
}