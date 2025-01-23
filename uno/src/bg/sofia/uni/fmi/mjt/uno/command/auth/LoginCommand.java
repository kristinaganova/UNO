package bg.sofia.uni.fmi.mjt.uno.command.auth;

import bg.sofia.uni.fmi.mjt.uno.command.AbstractCommand;
import bg.sofia.uni.fmi.mjt.uno.command.CommandValidator;
import bg.sofia.uni.fmi.mjt.uno.exceptions.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.player.account.UserManager;

import java.nio.channels.SocketChannel;

public class LoginCommand extends AbstractCommand {
    private final UserManager userManager;
    private final SocketChannel client;

    private static final String USAGE = "login --username=<username> --password=<password>";

    public LoginCommand(UserManager userManager, SocketChannel client) {
        this.userManager = userManager;
        this.client = client;
    }

    @Override
    protected String executeCommand(String[] args) {
        CommandValidator.validateArgsLength(args, 2, USAGE);

        String username = args[0];
        String password = args[1];

        if (userManager.isLoggedIn(client)) {
            throw new CommandExecutionException("You are already logged in.");
        }

        if (!userManager.validateCredentials(username, password)) {
            throw new CommandExecutionException("Invalid username or password.");
        }

        userManager.login(client, username);
        return "Login successful.";
    }
}

