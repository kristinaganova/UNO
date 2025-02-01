package bg.sofia.uni.fmi.mjt.uno.command;

import bg.sofia.uni.fmi.mjt.uno.command.factory.CommandFactory;
import bg.sofia.uni.fmi.mjt.uno.exceptions.command.CommandExecutionException;

import java.nio.channels.SocketChannel;

public class CommandExecutor {

    private final CommandFactory commandFactory;

    public CommandExecutor(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    public String executeCommand(String commandName, String[] args, SocketChannel client) {
        try {
            Command command = commandFactory.createCommand(commandName, client);
            return command.execute(commandName, args);
        } catch (CommandExecutionException e) {
            return "Command execution error: " + e.getMessage();
        } catch (IllegalArgumentException e) {
            return "Invalid command: " + e.getMessage();
        } catch (Exception e) {
            return "Unexpected error occurred: " + e.getMessage();
        }
    }
}