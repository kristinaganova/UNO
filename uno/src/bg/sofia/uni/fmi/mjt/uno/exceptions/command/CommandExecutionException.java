package bg.sofia.uni.fmi.mjt.uno.exceptions.command;

public class CommandExecutionException extends RuntimeException {
    public CommandExecutionException(String message) {
        super(message);
    }

    public CommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
