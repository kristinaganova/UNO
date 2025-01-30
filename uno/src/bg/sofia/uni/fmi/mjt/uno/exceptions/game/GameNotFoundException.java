package bg.sofia.uni.fmi.mjt.uno.exceptions.game;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String s) {
        super(s);
    }

    public GameNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
