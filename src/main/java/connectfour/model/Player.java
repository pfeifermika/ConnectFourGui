package connectfour.model;

/**
 * Models a player in a Connect Four game.
 */
public enum Player {

    /**
     * Human player.
     */
    HUMAN("X"),

    /**
     * Machine player.
     */
    MACHINE("O"),

    /**
     * Neither human nor machine.
     */
    TIE(".");

    /**
     * The respective char representation.
     */
    private final String symbol;

    Player(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Provides the opposite player of the given one.
     *
     * @param player the player.
     * @return the opposite to the given player.
     */
    public static Player oppositePlayer(Player player) {
        if (player == HUMAN) {
            return MACHINE;
        } else if (player == MACHINE) {
            return HUMAN;
        } else {
            return TIE;
        }
    }

    /**
     * Returns the string representation of a player as defined in the symbol
     * field.
     *
     * @return the string representation of a player.
     */
    @Override
    public String toString() {
        return symbol;
    }
}
