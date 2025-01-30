package bg.sofia.uni.fmi.mjt.uno.command.factory;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.command.management.CreateGameCommand;
import bg.sofia.uni.fmi.mjt.uno.command.management.JoinCommand;
import bg.sofia.uni.fmi.mjt.uno.command.management.ListGamesCommand;
import bg.sofia.uni.fmi.mjt.uno.command.management.StartCommand;
import bg.sofia.uni.fmi.mjt.uno.command.management.SummaryCommand;
import bg.sofia.uni.fmi.mjt.uno.exceptions.command.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.uno.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.player.account.UserManager;

import java.nio.channels.SocketChannel;

public class GameManagementCommandFactory {
    private final UserManager userManager;
    private final GameManager gameManager;

    public GameManagementCommandFactory(UserManager userManager, GameManager gameManager) {
        this.userManager = userManager;
        this.gameManager = gameManager;
    }

    public boolean supports(String commandName) {
        return switch (commandName) {
            case "create-game", "list-games", "join", "start", "summary" -> true;
            default -> false;
        };
    }

    public Command createCommand(String commandName, SocketChannel client) {
        return switch (commandName) {
            case "create-game" -> new CreateGameCommand(gameManager, userManager, client);
            case "list-games" -> new ListGamesCommand(gameManager);
            case "join" -> new JoinCommand(gameManager, userManager, client);
            case "start" -> new StartCommand(gameManager, userManager, client);
            case "summary" -> new SummaryCommand(gameManager);
            default -> throw new CommandNotFoundException("Unknown command: " + commandName);
        };
    }
}
