package connectfour.model;

public record Coordinates2D(int row, int col) implements Comparable<Coordinates2D> {


    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }

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

/*

    if this < o -> return -1

    if this == o -> return 0

    if this > o -> return 1



 */
