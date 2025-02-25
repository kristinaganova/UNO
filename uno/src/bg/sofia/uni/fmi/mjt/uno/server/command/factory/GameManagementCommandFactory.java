package bg.sofia.uni.fmi.mjt.uno.server.command.factory;

import bg.sofia.uni.fmi.mjt.uno.server.command.Command;
import bg.sofia.uni.fmi.mjt.uno.server.command.factory.types.GameManagementCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.management.CreateGameCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.management.JoinCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.management.ListGamesCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.management.StartCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.management.SummaryCommand;
import bg.sofia.uni.fmi.mjt.uno.server.exceptions.command.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.uno.server.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.server.player.account.UserManager;

import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class GameManagementCommandFactory {
    private final UserManager userManager;
    private final GameManager gameManager;

    public GameManagementCommandFactory(UserManager userManager, GameManager gameManager) {
        this.userManager = userManager;
        this.gameManager = gameManager;
    }

    public boolean supports(String commandName) {
        return Arrays.stream(GameManagementCommand.values())
                .anyMatch(command -> command.getCommand().equals(commandName));
    }

    public Command createCommand(String commandName, SocketChannel client) {
        if (!supports(commandName)) {
            throw new CommandNotFoundException(commandName);
        }

        for (GameManagementCommand command : GameManagementCommand.values()) {
            if (command.getCommand().equals(commandName)) {
                return switch (command) {
                    case CREATE -> new CreateGameCommand(gameManager, userManager, client);
                    case LIST -> new ListGamesCommand(gameManager);
                    case JOIN -> new JoinCommand(gameManager, userManager, client);
                    case START -> new StartCommand(gameManager, userManager, client);
                    case SUMMARY -> new SummaryCommand(gameManager);
                };
            }
        }
        throw new CommandNotFoundException("Unknown command: " + commandName);
    }
}