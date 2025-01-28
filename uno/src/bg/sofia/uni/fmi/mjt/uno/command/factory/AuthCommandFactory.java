package bg.sofia.uni.fmi.mjt.uno.command.factory;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.command.auth.LoginCommand;
import bg.sofia.uni.fmi.mjt.uno.command.auth.LogoutCommand;
import bg.sofia.uni.fmi.mjt.uno.command.auth.RegisterCommand;
import bg.sofia.uni.fmi.mjt.uno.exceptions.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.uno.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.player.account.UserManager;

import java.nio.channels.SocketChannel;

public class AuthCommandFactory {
    private final UserManager userManager;
    private final GameManager gameManager;

    public AuthCommandFactory(UserManager userManager, GameManager gameManager) {
        this.userManager = userManager;
        this.gameManager = gameManager;
    }

    public boolean supports(String commandName) {
        return switch (commandName) {
            case "register", "login", "logout" -> true;
            default -> false;
        };
    }

    public Command createCommand(String commandName, SocketChannel client) {
        return switch (commandName) {
            case "register" -> new RegisterCommand(userManager);
            case "login" -> new LoginCommand(userManager, gameManager, client);
            case "logout" -> new LogoutCommand(userManager, client);
            default -> throw new CommandNotFoundException("Unknown command: " + commandName);
        };
    }
}
