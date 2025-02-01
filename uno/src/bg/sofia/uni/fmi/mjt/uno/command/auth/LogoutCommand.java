package bg.sofia.uni.fmi.mjt.uno.command.auth;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.player.account.UserManager;

import java.nio.channels.SocketChannel;

public class LogoutCommand implements Command {
    private final UserManager userManager;
    private final SocketChannel client;

    public LogoutCommand(UserManager userManager, SocketChannel client) {
        this.userManager = userManager;
        this.client = client;
    }

    @Override
    public String execute(String commandName, String[] args) {
        if (!userManager.isLoggedIn(client)) {
            return "You are not logged in.";
        }

        userManager.logout(client);
        return "Logout successful.";
    }
}