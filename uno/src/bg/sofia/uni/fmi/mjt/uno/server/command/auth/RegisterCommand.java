package bg.sofia.uni.fmi.mjt.uno.server.command.auth;

import bg.sofia.uni.fmi.mjt.uno.server.command.AbstractCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.CommandValidator;
import bg.sofia.uni.fmi.mjt.uno.server.exceptions.command.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.server.player.account.UserManager;

public class RegisterCommand extends AbstractCommand {
    private final UserManager userManager;

    private static final String USAGE = "register <username> <password>";

    public RegisterCommand(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    protected String executeCommand(String[] args) {
        CommandValidator.validateArgsLength(args, 2, USAGE);

        String username = CommandValidator.extractArgument(args, "--username=", USAGE);
        String password = CommandValidator.extractArgument(args, "--password=", USAGE);

        if (userManager.createAccount(username, password)) {
            return "User registered successfully.";
        } else {
            throw new CommandExecutionException("Username already exists.");
        }
    }
}

