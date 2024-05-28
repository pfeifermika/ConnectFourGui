package connectfour.model.exceptions;

/**
 * Thrown to indicate that a move has been invoked in an illegal state.
 */
public class IllegalMoveException extends RuntimeException {

    /**
     * Constructs an IllegalMoveException with no detail message.
     */
    public IllegalMoveException() {
        super();
    }

    /**
     * Constructs an IllegalMoveException with the specified detail message.
     * @param errorMessage the String that contains a detailed message
     */
    public IllegalMoveException(String errorMessage) {
        super(errorMessage);
    }

}