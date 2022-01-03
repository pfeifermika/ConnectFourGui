package connectfour.model;

/**
 * Models a player in a Connect Four game.
 */
public enum Player {

    /** Human player */
    HUMAN("X"),
    /** Computer player */
    COMPUTER("O"),
    /** Neither human nor computer */
    TIE(".");

    private final String symbol;

    Player(String symbol){
        this.symbol = symbol;
    }

    /**
     * Provides the opposite player of the given one.
     *
     * @param player the player.
     * @return the opposite to the given player.
     */
    public static Player getOppositePlayer(Player player){
        if(player == HUMAN){
            return COMPUTER;
        }else if(player == COMPUTER){
            return HUMAN;
        }else {
            return TIE;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return symbol;
    }
}
