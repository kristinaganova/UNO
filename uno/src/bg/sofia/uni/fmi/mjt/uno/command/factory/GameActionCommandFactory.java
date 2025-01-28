package bg.sofia.uni.fmi.mjt.uno.command.factory;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.command.game.actions.DrawCardCommand;
import bg.sofia.uni.fmi.mjt.uno.command.game.actions.LeaveCommand;
import bg.sofia.uni.fmi.mjt.uno.command.game.actions.play.PlayCardCommand;
import bg.sofia.uni.fmi.mjt.uno.command.game.actions.play.PlayChooseColorCommand;
import bg.sofia.uni.fmi.mjt.uno.command.game.actions.play.PlayPlusFourCommand;
import bg.sofia.uni.fmi.mjt.uno.command.game.info.SpectateCommand;
import bg.sofia.uni.fmi.mjt.uno.exceptions.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.player.Player;
import bg.sofia.uni.fmi.mjt.uno.player.account.UserManager;

import java.nio.channels.SocketChannel;

public class GameActionCommandFactory {
    private final UserManager userManager;
    private final GameManager gameManager;

    public GameActionCommandFactory(UserManager userManager, GameManager gameManager) {
        this.userManager = userManager;
        this.gameManager = gameManager;
    }

    public boolean supports(String commandName) {
        return switch (commandName) {
            case "play-card", "play-choose-color", "play-plus-four", "draw-card", "leave", "spectate" -> true;
            default -> false;
        };
    }

    public Command createCommand(String commandName, SocketChannel client) {
        Player player = getPlayer(client);
        Game game = getGame(client);

        return switch (commandName) {
            case "play-card" -> new PlayCardCommand(player, game);
            case "play-choose-color" -> new PlayChooseColorCommand(player, game);
            case "play-plus-four" -> new PlayPlusFourCommand(player, game);
            case "draw-card" -> new DrawCardCommand(player, game);
            case "leave" -> new LeaveCommand(player, game);
            case "spectate" -> new SpectateCommand(player, game);
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
