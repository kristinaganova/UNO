package bg.sofia.uni.fmi.mjt.uno.exceptions.game;

public class GameNotAvailableException extends RuntimeException {
    public GameNotAvailableException(String message) {
        super(message);
    }

    public GameNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
