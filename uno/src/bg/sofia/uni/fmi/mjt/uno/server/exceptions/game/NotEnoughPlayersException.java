package bg.sofia.uni.fmi.mjt.uno.server.exceptions.game;

public class NotEnoughPlayersException extends RuntimeException {
    public NotEnoughPlayersException(String message) {
        super(message);
    }
}

