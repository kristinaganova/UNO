package bg.sofia.uni.fmi.mjt.uno.command;

import bg.sofia.uni.fmi.mjt.uno.exceptions.CommandExecutionException;

import java.nio.channels.SocketChannel;

public class CommandExecutor {

    private final CommandFactory commandFactory;

    public CommandExecutor(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    public String executeCommand(String commandName, String[] args, SocketChannel client) {
        try {
            Command command = commandFactory.createCommand(commandName, client);
            return command.execute(args);
        } catch (CommandExecutionException e) {
            return createErrorResponse("Command execution error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return createErrorResponse("Invalid command: " + e.getMessage());
        } catch (Exception e) {
            return createErrorResponse("Unexpected error occurred: " + e.getMessage());
        }
    }

    private String createErrorResponse(String message) {
        return String.format("ERROR: ", message);
    }
}
