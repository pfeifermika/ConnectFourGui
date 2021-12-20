package connectfour.model;

public record Coordinates2D(int row, int col) {


    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}
