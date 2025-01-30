package bg.sofia.uni.fmi.mjt.uno.exceptions.deck;

public class IllegalDeckSateException extends RuntimeException {
    public IllegalDeckSateException(String message) {
        super(message);
    }

    public IllegalDeckSateException(String message, Throwable cause) {
        super(message, cause);
    }
}
