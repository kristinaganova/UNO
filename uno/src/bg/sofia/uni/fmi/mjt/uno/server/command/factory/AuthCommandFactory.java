package bg.sofia.uni.fmi.mjt.uno.server.command.factory;

import bg.sofia.uni.fmi.mjt.uno.server.command.Command;
import bg.sofia.uni.fmi.mjt.uno.server.command.auth.LoginCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.auth.LogoutCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.auth.RegisterCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.factory.types.AuthCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.factory.types.GameActionCommand;
import bg.sofia.uni.fmi.mjt.uno.server.exceptions.command.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.uno.server.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.server.player.account.UserManager;

import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class AuthCommandFactory {
    private final UserManager userManager;
    private final GameManager gameManager;

    public AuthCommandFactory(UserManager userManager, GameManager gameManager) {
        this.userManager = userManager;
        this.gameManager = gameManager;
    }

    public boolean supports(String commandName) {
        return Arrays.stream(AuthCommand.values())
                .anyMatch(command -> command.getCommand().equals(commandName));
    }

    public Command createCommand(String commandName, SocketChannel client) {
        if (!supports(commandName)) {
            throw new CommandNotFoundException(commandName);
        }

        for (AuthCommand command : AuthCommand.values()) {
            if (command.getCommand().equals(commandName)) {
                return switch (command) {
                    case REGISTER -> new RegisterCommand(userManager);
                    case LOGIN -> new LoginCommand(userManager, gameManager, client);
                    case LOGOUT -> new LogoutCommand(userManager, client);
                };
            }
        }

        throw new CommandNotFoundException("Unknown command: " + commandName);
    }
}
