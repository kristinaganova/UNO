package bg.sofia.uni.fmi.mjt.uno.command.auth;

import bg.sofia.uni.fmi.mjt.uno.command.AbstractCommand;
import bg.sofia.uni.fmi.mjt.uno.command.CommandValidator;
import bg.sofia.uni.fmi.mjt.uno.exceptions.command.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.player.account.UserManager;

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

