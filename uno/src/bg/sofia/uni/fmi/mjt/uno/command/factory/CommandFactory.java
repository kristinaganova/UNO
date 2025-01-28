package bg.sofia.uni.fmi.mjt.uno.command.factory;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.exceptions.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.uno.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.player.account.UserManager;

import java.nio.channels.SocketChannel;

public class CommandFactory {

    private final AuthCommandFactory authCommandFactory;
    private final GameManagementCommandFactory gameManagementCommandFactory;
    private final GameActionCommandFactory gameActionCommandFactory;
    private final GameInfoCommandFactory gameInfoCommandFactory;

    public CommandFactory(UserManager userManager, GameManager gameManager) {
        this.authCommandFactory = new AuthCommandFactory(userManager, gameManager);
        this.gameManagementCommandFactory = new GameManagementCommandFactory(userManager, gameManager);
        this.gameActionCommandFactory = new GameActionCommandFactory(userManager, gameManager);
        this.gameInfoCommandFactory = new GameInfoCommandFactory(userManager, gameManager);
    }

    public Command createCommand(String commandName, SocketChannel client) {
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
