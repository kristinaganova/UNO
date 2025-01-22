package bg.sofia.uni.fmi.mjt.uno.command.auth;

import bg.sofia.uni.fmi.mjt.uno.command.AbstractCommand;
import bg.sofia.uni.fmi.mjt.uno.command.CommandValidator;
import bg.sofia.uni.fmi.mjt.uno.exceptions.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.player.account.UserManager;

public class RegisterCommand extends AbstractCommand {
    private final UserManager userManager;

    public RegisterCommand(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    protected String executeCommand(String[] args) {
        CommandValidator.validateArgsLength(args, 2, "register <username> <password>");

        String username = args[0];
        String password = args[1];

        if (userManager.createAccount(username, password)) {
            return "User registered successfully.";
        } else {
            throw new CommandExecutionException("Username already exists.");
        }
    }
}

