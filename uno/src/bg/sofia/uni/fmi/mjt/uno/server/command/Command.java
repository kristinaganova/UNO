package bg.sofia.uni.fmi.mjt.uno.server.command;

public interface Command {
    String execute(String commandName, String[] args);
}
