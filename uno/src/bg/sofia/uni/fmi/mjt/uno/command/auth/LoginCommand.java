package bg.sofia.uni.fmi.mjt.uno.command.auth;

import bg.sofia.uni.fmi.mjt.uno.command.AbstractCommand;
import bg.sofia.uni.fmi.mjt.uno.command.CommandValidator;
import bg.sofia.uni.fmi.mjt.uno.exceptions.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.player.account.UserManager;

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

        if (gameManager.reconnectPlayer(username)) {
            return "Welcome back, " + username + "! You have been reconnected to your ongoing game.";
        }

        userManager.login(client, username);
        return "Login successful.";
    }

}

