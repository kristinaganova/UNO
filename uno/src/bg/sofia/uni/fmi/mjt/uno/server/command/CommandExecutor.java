package bg.sofia.uni.fmi.mjt.uno.server.command;

import bg.sofia.uni.fmi.mjt.uno.server.command.factory.CommandFactory;
import bg.sofia.uni.fmi.mjt.uno.server.exceptions.command.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.server.game.Game;
import bg.sofia.uni.fmi.mjt.uno.server.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.server.player.account.UserManager;

import java.nio.channels.SocketChannel;

public class CommandExecutor {

    private final CommandFactory commandFactory;

    public CommandExecutor(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    public String executeCommand(String commandName, String[] args, SocketChannel client) {
        try {
            Command command = commandFactory.createCommand(commandName, client);
            String response = command.execute(commandName, args);

            notifyMonitor(client);

            return response;
        } catch (CommandExecutionException e) {
            return "Command execution error: " + e.getMessage();
        } catch (IllegalArgumentException e) {
            return "Invalid command: " + e.getMessage();
        } catch (Exception e) {
            return "Unexpected error occurred: " + e.getMessage();
        }
    }

    private void notifyMonitor(SocketChannel client) {
        UserManager userManager = UserManager.getInstance();
        String username = userManager.getLoggedInUsername(client);
        Game game = GameManager.getInstance().getGameByPlayer(username);

        if (game != null && game.getGameMonitor() != null) {
            game.getGameMonitor().wakeUp();
        }
    }
}
