package connectfour.model;

public enum Player {

    HUMAN(1,"X"),
    COMPUTER(2,"O"),
    TIE(0,".");

    Player(int id,String symbol){
        this.id = id;
        this.symbol = symbol;
    }

    private final int id;
    private final String symbol;

    @Override
    public String toString() {
        return symbol;
    }
}
