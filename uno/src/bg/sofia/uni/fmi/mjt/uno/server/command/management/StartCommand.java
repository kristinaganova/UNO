package bg.sofia.uni.fmi.mjt.uno.server.command.management;

import bg.sofia.uni.fmi.mjt.uno.server.command.AbstractCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.CommandValidator;
import bg.sofia.uni.fmi.mjt.uno.server.exceptions.command.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.server.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.server.player.Player;
import bg.sofia.uni.fmi.mjt.uno.server.player.account.UserManager;

import java.nio.channels.SocketChannel;

public class StartCommand extends AbstractCommand {
    private static final String USAGE = "start --game-id=<game-id>";
    private final GameManager gameManager;
    private final UserManager userManager;
    private final SocketChannel client;

    public StartCommand(GameManager gameManager, UserManager userManager, SocketChannel client) {
        this.gameManager = gameManager;
        this.userManager = userManager;
        this.client = client;
    }

    @Override
    protected String executeCommand(String[] args) {
        CommandValidator.validateArgsLength(args, 1, USAGE);
        String gameId = CommandValidator.extractArgument(args, "--game-id=", USAGE);

        if (!userManager.isLoggedIn(client)) {
            throw new CommandExecutionException("You must be logged in to start a game.");
        }

        String username = userManager.getLoggedInUsername(client);
        Player requestingPlayer = userManager.getPlayerByUsername(username);

        if (requestingPlayer == null) {
            throw new CommandExecutionException("You must join the game before starting it.");
        }

        boolean started = gameManager.startGame(gameId, requestingPlayer);
        if (started) {
            return "Game " + gameId + " started successfully by " + username + ".";
        } else {
            throw new CommandExecutionException("Game " + gameId + " could not be started.");
        }
    }
}
