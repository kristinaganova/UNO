package bg.sofia.uni.fmi.mjt.uno.exceptions.game;

public class NoOnlinePlayersException extends RuntimeException {
    public NoOnlinePlayersException(String message) {
        super(message);
    }
}
