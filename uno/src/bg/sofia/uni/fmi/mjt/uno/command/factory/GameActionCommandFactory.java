package bg.sofia.uni.fmi.mjt.uno.command.factory;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.command.game.actions.DrawCardCommand;
import bg.sofia.uni.fmi.mjt.uno.command.game.actions.KeepCommand;
import bg.sofia.uni.fmi.mjt.uno.command.game.actions.LeaveCommand;
import bg.sofia.uni.fmi.mjt.uno.command.game.actions.StopUnoCommand;
import bg.sofia.uni.fmi.mjt.uno.command.game.actions.UnoCommand;
import bg.sofia.uni.fmi.mjt.uno.command.game.actions.play.PlayCardCommand;
import bg.sofia.uni.fmi.mjt.uno.command.game.actions.play.PlayChooseColorCommand;
import bg.sofia.uni.fmi.mjt.uno.command.game.actions.play.PlayPlusFourCommand;
import bg.sofia.uni.fmi.mjt.uno.command.game.info.SpectateCommand;
import bg.sofia.uni.fmi.mjt.uno.exceptions.command.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;
import bg.sofia.uni.fmi.mjt.uno.player.account.UserManager;

import java.nio.channels.SocketChannel;

public class GameActionCommandFactory {
    private final UserManager userManager;

    public GameActionCommandFactory(UserManager userManager) {
        this.userManager = userManager;
    }

    public boolean supports(String commandName) {
        return switch (commandName) {
            case "play-card", "play-choose-color", "play-plus-four", "draw-card",
                 "leave", "spectate", "uno", "stop-uno", "keep"-> true;
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
            case "uno" -> new UnoCommand(player, game);
            case "stop-uno" -> new StopUnoCommand(player, game);
            case "keep" -> new KeepCommand(player, game);
            default -> throw new CommandNotFoundException("Unknown command: " + commandName);
        };
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
