package bg.sofia.uni.fmi.mjt.uno.server.command.factory;

import bg.sofia.uni.fmi.mjt.uno.server.command.Command;
import bg.sofia.uni.fmi.mjt.uno.server.command.factory.types.GameInfoCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.game.info.ShowHandCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.game.info.ShowLastCardCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.game.info.ShowPlayedCardsCommand;
import bg.sofia.uni.fmi.mjt.uno.server.exceptions.command.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.Game;
import bg.sofia.uni.fmi.mjt.uno.server.player.Player;
import bg.sofia.uni.fmi.mjt.uno.server.player.account.UserManager;

import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class GameInfoCommandFactory {
    private final UserManager userManager;

    public GameInfoCommandFactory(UserManager userManager) {
        this.userManager = userManager;
    }

    public boolean supports(String commandName) {
        return Arrays.stream(GameInfoCommand.values())
                .anyMatch(command -> command.getCommand().equals(commandName));
    }

    public Command createCommand(String commandName, SocketChannel client) {
        Player player = getPlayer(client);
        Game game = getGame(client);

        if (!supports(commandName)) {
            throw new CommandNotFoundException(commandName);
        }

        for (GameInfoCommand command : GameInfoCommand.values()) {
            if (command.getCommand().equals(commandName)) {
                return switch (command) {
                    case SHOW_HAND -> new ShowHandCommand(player, game);
                    case SHOW_LAST_CARD -> new ShowLastCardCommand(player, game);
                    case SHOW_PLAYED_CARDS -> new ShowPlayedCardsCommand(player, game);
                };
            }
        }

        throw new CommandNotFoundException("Unknown command: " + commandName);
    }

    private Player getPlayer(SocketChannel client) {
        String username = userManager.getLoggedInUsername(client);
        if (username == null) {
            throw new IllegalArgumentException("Client is not logged in.");
        }

        Player player = userManager.getPlayerByUsername(username);
        if (player == null) {
            throw new IllegalArgumentException("No player found for the logged-in user.");
        }

        return player;
    }

    private Game getGame(SocketChannel client) {
        Player player = getPlayer(client);
        if (player == null) {
            throw new IllegalArgumentException("Player is not logged in.");
        }

        Game game = player.getCurrentGame();
        if (game == null) {
            throw new IllegalArgumentException("Player is not part of any game.");
        }

        return game;
    }
}
