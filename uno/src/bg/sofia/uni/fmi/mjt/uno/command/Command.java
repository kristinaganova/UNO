package bg.sofia.uni.fmi.mjt.uno.command;

public interface Command {
    /**
     * Executes the command logic.
     *
     * @param commandName
     * @param args        Arguments required for the command.
     * @return A response string to send back to the client.
     */
    String execute(String commandName, String[] args);
}
