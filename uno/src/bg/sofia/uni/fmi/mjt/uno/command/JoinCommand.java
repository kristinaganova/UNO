package bg.sofia.uni.fmi.mjt.uno.command;

import bg.sofia.uni.fmi.mjt.uno.exceptions.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.game.GameManager;
import bg.sofia.uni.fmi.mjt.uno.player.Player;
import bg.sofia.uni.fmi.mjt.uno.player.account.UserManager;

import java.nio.channels.SocketChannel;

public class JoinCommand extends AbstractCommand {
    private final GameManager gameManager;
    private final UserManager userManager;
    private final SocketChannel client;

    private static final String USAGE = "join <game-id>";

    public JoinCommand(GameManager gameManager, UserManager userManager, SocketChannel client) {
        this.gameManager = gameManager;
        this.userManager = userManager;
        this.client = client;
    }

    @Override
    protected String executeCommand(String[] args) {
        CommandValidator.validateArgsLength(args, 1, USAGE);
        String gameId = CommandValidator.extractArgument(args, "--game-id=", USAGE);

        if (!userManager.isLoggedIn(client)) {
            throw new CommandExecutionException("You must be logged in to join a game.");
        }

        String username = userManager.getLoggedInUsername(client);
        if (gameManager.isPlayerInAnyGame(username)) {
            throw new CommandExecutionException("You are already part of another game. You must leave it before joining a new one.");
        }

        if (!gameManager.doesGameExist(gameId)) {
            throw new CommandExecutionException("Game with ID " + gameId + " does not exist.");
        }

        Player player = userManager.getPlayerByUsername(username);
        if (player == null) {
            throw new CommandExecutionException("Player instance not found for the logged-in user.");
        }

        if (!gameManager.joinGame(gameId, player)) {
            throw new CommandExecutionException("Unable to join game. It may be full or already started.");
        }

        player.setGame(gameManager.getGame(gameId));

        return "Successfully joined game with ID: " + gameId;
    }
}
