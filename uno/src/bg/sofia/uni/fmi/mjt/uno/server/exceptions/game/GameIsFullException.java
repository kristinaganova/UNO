package bg.sofia.uni.fmi.mjt.uno.server.exceptions.game;

public class GameIsFullException extends RuntimeException {
    public GameIsFullException(String message) {
        super(message);
    }
}
