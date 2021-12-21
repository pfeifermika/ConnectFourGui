package connectfour.model;

public enum Player {

    HUMAN("X"),
    COMPUTER("O"),
    TIE(".");

    Player(String symbol){
        this.symbol = symbol;
    }

    private final String symbol;

    public static Player getNextPlayer(Player player){
        if(player == HUMAN){
            return COMPUTER;
        }else if(player == COMPUTER){
            return HUMAN;
        }else {
            return TIE;
        }
    }

    @Override
    public String toString() {
        return symbol;
    }
}
