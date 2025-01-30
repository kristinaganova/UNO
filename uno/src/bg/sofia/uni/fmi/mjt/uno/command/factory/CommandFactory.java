package bg.sofia.uni.fmi.mjt.uno.command.factory;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.exceptions.command.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.exceptions.command.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.uno.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.player.account.UserManager;

import java.nio.channels.SocketChannel;

public class CommandFactory {

    private final AuthCommandFactory authCommandFactory;
    private final GameManagementCommandFactory gameManagementCommandFactory;
    private final GameActionCommandFactory gameActionCommandFactory;
    private final GameInfoCommandFactory gameInfoCommandFactory;

    public CommandFactory(UserManager userManager, GameManager gameManager) {
        if (userManager == null) {
            throw new CommandExecutionException("User manager is null");
        }
        if (gameManager == null) {
            throw new CommandExecutionException("Game manager is null");
        }
        this.authCommandFactory = new AuthCommandFactory(userManager, gameManager);
        this.gameManagementCommandFactory = new GameManagementCommandFactory(userManager, gameManager);
        this.gameActionCommandFactory = new GameActionCommandFactory(userManager);
        this.gameInfoCommandFactory = new GameInfoCommandFactory(userManager);
    }

    public Command createCommand(String commandName, SocketChannel client) {
        if (client == null) {
            throw new CommandExecutionException("Client is null");
        }

        if (commandName == null || commandName.isEmpty()) {
            throw new CommandNotFoundException("Command name is null or empty");
        }

        if (authCommandFactory.supports(commandName)) {
            return authCommandFactory.createCommand(commandName, client);
        } else if (gameManagementCommandFactory.supports(commandName)) {
            return gameManagementCommandFactory.createCommand(commandName, client);
        } else if (gameActionCommandFactory.supports(commandName)) {
            return gameActionCommandFactory.createCommand(commandName, client);
        } else if (gameInfoCommandFactory.supports(commandName)) {
            return gameInfoCommandFactory.createCommand(commandName, client);
        }

        throw new CommandNotFoundException("Unknown command: " + commandName);
    }
}
