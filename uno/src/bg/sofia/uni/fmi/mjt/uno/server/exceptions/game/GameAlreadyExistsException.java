package bg.sofia.uni.fmi.mjt.uno.server.exceptions.game;

public class GameAlreadyExistsException extends RuntimeException {

    public GameAlreadyExistsException(String message) {
        super(message);
    }
}
