package bg.sofia.uni.fmi.mjt.uno.server.command;

public abstract class AbstractCommand implements Command {
    @Override
    public String execute(String commandName, String[] args) {
        try {
            return executeCommand(args);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    protected abstract String executeCommand(String[] args);
}
