package bg.sofia.uni.fmi.mjt.uno.server.command.factory;

import bg.sofia.uni.fmi.mjt.uno.server.command.Command;
import bg.sofia.uni.fmi.mjt.uno.server.command.factory.types.GameActionCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.game.actions.DrawCardCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.game.actions.KeepCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.game.actions.LeaveCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.game.actions.StopUnoCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.game.actions.UnoCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.game.actions.play.PlayCardCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.game.actions.play.PlayChooseColorCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.game.actions.play.PlayPlusFourCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.game.info.SpectateCommand;
import bg.sofia.uni.fmi.mjt.uno.server.exceptions.command.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.Game;
import bg.sofia.uni.fmi.mjt.uno.server.player.Player;
import bg.sofia.uni.fmi.mjt.uno.server.player.account.UserManager;

import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class GameActionCommandFactory {
    private final UserManager userManager;

    public GameActionCommandFactory(UserManager userManager) {
        this.userManager = userManager;
    }

    public boolean supports(String commandName) {
        return Arrays.stream(GameActionCommand.values())
                .anyMatch(command -> command.getCommand().equals(commandName));
    }

    public Command createCommand(String commandName, SocketChannel client) {
        Player player = getPlayer(client);
        Game game = getGame(client);

        if (!supports(commandName)) {
            throw new CommandNotFoundException(commandName);
        }

        for (GameActionCommand command : GameActionCommand.values()) {
            if (command.getCommand().equals(commandName)) {
                return switch (command) {
                    case PLAY_CARD -> new PlayCardCommand(player, game);
                    case PLAY_CHOOSE_COLOR -> new PlayChooseColorCommand(player, game);
                    case PLAY_PLUS_FOUR -> new PlayPlusFourCommand(player, game);
                    case DRAW_CARD -> new DrawCardCommand(player, game);
                    case LEAVE -> new LeaveCommand(player, game);
                    case SPECTATE -> new SpectateCommand(player, game);
                    case UNO -> new UnoCommand(player, game);
                    case STOP_UNO -> new StopUnoCommand(player, game);
                    case KEEP -> new KeepCommand(player, game);
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
