package connectfour.model;

public record Coordinates2D(int x, int y) {


    @Override
    public String toString() {
        return "(" + (x + 1) + ", " + (y + 1) + ")";
    }
}
