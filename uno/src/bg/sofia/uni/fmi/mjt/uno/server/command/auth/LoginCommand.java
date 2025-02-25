package bg.sofia.uni.fmi.mjt.uno.server.command.auth;

import bg.sofia.uni.fmi.mjt.uno.server.command.AbstractCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.CommandValidator;
import bg.sofia.uni.fmi.mjt.uno.server.exceptions.command.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.Game;
import bg.sofia.uni.fmi.mjt.uno.server.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.server.player.account.UserManager;

import java.nio.channels.SocketChannel;

public class LoginCommand extends AbstractCommand {
    private final UserManager userManager;
    private final SocketChannel client;
    private final GameManager gameManager;

    private static final String USAGE = "login --username=<username> --password=<password>";

    public LoginCommand(UserManager userManager, GameManager gameManager, SocketChannel client) {
        this.userManager = userManager;
        this.client = client;
        this.gameManager = gameManager;
    }

    @Override
    protected String executeCommand(String[] args) {
        CommandValidator.validateArgsLength(args, 2, USAGE);

        String username = CommandValidator.extractArgument(args, "--username=", USAGE);
        String password = CommandValidator.extractArgument(args, "--password=", USAGE);

        if (userManager.isLoggedIn(client)) {
            throw new CommandExecutionException("You are already logged in.");
        }

        if (!userManager.validateCredentials(username, password)) {
            throw new CommandExecutionException("Invalid username or password.");
        }

        userManager.login(client, username);

        Game currentGame = gameManager.getGameByPlayer(username);

        if (currentGame != null) {
            if (currentGame.reconnectPlayer(username)) {
                return "Welcome back, " + username + "! You have been reconnected to your ongoing game.";
            } else {
                return "Failed to reconnect you to your game.";
            }
        }

        return "Login successful. No ongoing game found.";
    }
}
