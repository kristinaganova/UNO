package bg.sofia.uni.fmi.mjt.uno.server.exceptions.player;

public class PlayerAlreadyInGameException extends RuntimeException {
    public PlayerAlreadyInGameException(String message) {
        super(message);
    }
}
