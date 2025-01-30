package bg.sofia.uni.fmi.mjt.uno.exceptions.game;

public class GameAlreadyExistsException extends RuntimeException {

    public GameAlreadyExistsException(String message) {
        super(message);
    }

    public GameAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
