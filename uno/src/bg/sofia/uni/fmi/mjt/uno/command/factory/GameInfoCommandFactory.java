package bg.sofia.uni.fmi.mjt.uno.command.factory;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.command.game.info.ShowHandCommand;
import bg.sofia.uni.fmi.mjt.uno.command.game.info.ShowLastCardCommand;
import bg.sofia.uni.fmi.mjt.uno.command.game.info.ShowPlayedCardsCommand;
import bg.sofia.uni.fmi.mjt.uno.exceptions.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.player.Player;
import bg.sofia.uni.fmi.mjt.uno.player.account.UserManager;

import java.nio.channels.SocketChannel;

public class GameInfoCommandFactory {
    private final UserManager userManager;
    private final GameManager gameManager;

    public GameInfoCommandFactory(UserManager userManager, GameManager gameManager) {
        this.userManager = userManager;
        this.gameManager = gameManager;
    }

    public boolean supports(String commandName) {
        return switch (commandName) {
            case "show-hand", "show-last-card", "show-played-cards" -> true;
            default -> false;
        };
    }

    public Command createCommand(String commandName, SocketChannel client) {
        Player player = getPlayer(client);
        Game game = getGame(client);

        return switch (commandName) {
            case "show-hand" -> new ShowHandCommand(player, game);
            case "show-last-card" -> new ShowLastCardCommand(player, game);
            case "show-played-cards" -> new ShowPlayedCardsCommand(player, game);
            default -> throw new CommandNotFoundException("Unknown command: " + commandName);
        };
    }

    private Player getPlayer(SocketChannel client) {
        return userManager.getPlayerByUsername(userManager.getLoggedInUsername(client));
    }

    private Game getGame(SocketChannel client) {
        Player player = getPlayer(client);
        Game game = player.getCurrentGame();
        if (game == null) {
            throw new IllegalStateException("Player is not part of any game.");
        }
        return game;
    }
}
