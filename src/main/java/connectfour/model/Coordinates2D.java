package connectfour.model;

public class Coordinates2D {

    int x;
    int y;

    Coordinates2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
