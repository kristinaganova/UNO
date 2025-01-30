package bg.sofia.uni.fmi.mjt.uno.exceptions.player;

public class PlayerNotPermittedException extends RuntimeException {
    public PlayerNotPermittedException(String message) {
        super(message);
    }

    public PlayerNotPermittedException(String message, Throwable cause) {
        super(message, cause);
    }
}
