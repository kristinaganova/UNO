package bg.sofia.uni.fmi.mjt.uno.command.logged;

import bg.sofia.uni.fmi.mjt.uno.command.AbstractCommand;
import bg.sofia.uni.fmi.mjt.uno.command.CommandValidator;
import bg.sofia.uni.fmi.mjt.uno.exceptions.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.player.Player;
import bg.sofia.uni.fmi.mjt.uno.player.account.UserManager;

import java.nio.channels.SocketChannel;

public class CreateGameCommand extends AbstractCommand {
    private final GameManager gameManager;
    private final UserManager userManager;
    private final SocketChannel client;

    private static final String USAGE = "create-game --number-of-players=<number> --game-id=<game-id>";

    public CreateGameCommand(GameManager gameManager, UserManager userManager, SocketChannel client) {
        this.gameManager = gameManager;
        this.userManager = userManager;
        this.client = client;
    }

    @Override
    protected String executeCommand(String[] args) {
        CommandValidator.validateArgsLength(args, 2, USAGE);

        int numberOfPlayers = CommandValidator.extractIntArgument(args, "--number-of-players=", USAGE);
        String gameId = CommandValidator.extractArgument(args, "--game-id=", USAGE);

        if (!userManager.isLoggedIn(client)) {
            throw new CommandExecutionException("You must be logged in to create a game.");
        }

        String username = userManager.getLoggedInUsername(client);
        if (gameManager.isPlayerInAnyGame(username)) {
            throw new CommandExecutionException("You are already part of another game. " +
                    "Leave it before creating a new one.");
        }

        Player creator = userManager.getPlayerByUsername(username);

        if (creator == null) {
            throw new CommandExecutionException("Unable to retrieve player information for the logged-in user.");
        }

        if (gameManager.createGame(gameId, numberOfPlayers, creator)) {
            return "Game created successfully with ID: " + gameId + ". You have joined the game.";
        } else {
            throw new CommandExecutionException("Game creation failed. Game ID may already exist.");
        }
    }
}