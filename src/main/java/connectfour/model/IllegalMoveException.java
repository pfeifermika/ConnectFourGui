package connectfour.model;

public class IllegalMoveException extends RuntimeException {

    public IllegalMoveException() {
        super();
    }

    public IllegalMoveException(String errorMessage) {
        super(errorMessage);
    }


}
