package bg.sofia.uni.fmi.mjt.uno.exceptions.command;

public class CommandNotFoundException extends RuntimeException {
    public CommandNotFoundException(String message) {
        super(message);
    }
}
