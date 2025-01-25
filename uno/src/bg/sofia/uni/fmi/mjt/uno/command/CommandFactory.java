package bg.sofia.uni.fmi.mjt.uno.command;

import bg.sofia.uni.fmi.mjt.uno.command.auth.*;
import bg.sofia.uni.fmi.mjt.uno.command.game.*;
import bg.sofia.uni.fmi.mjt.uno.command.game.play.*;
import bg.sofia.uni.fmi.mjt.uno.exceptions.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.game.GameManager;
import bg.sofia.uni.fmi.mjt.uno.player.Player;
import bg.sofia.uni.fmi.mjt.uno.player.account.UserManager;

import java.nio.channels.SocketChannel;

public class CommandFactory {

    private final UserManager userManager;
    private final GameManager gameManager;

    public CommandFactory(UserManager userManager, GameManager gameManager) {
        this.userManager = userManager;
        this.gameManager = gameManager;
    }

    public Command createCommand(String commandName, SocketChannel client) {
        switch (commandName) {
            case "register":
                return new RegisterCommand(userManager);
            case "login":
                return new LoginCommand(userManager, client);
            case "logout":
                return new LogoutCommand(userManager, client);
            case "create-game":
                return new CreateGameCommand(gameManager, userManager, client);
            case "list-games":
                return new ListGamesCommand(gameManager);
            case "join":
                return new JoinCommand(gameManager, userManager, client);
            case "start":
                return new StartCommand(gameManager, userManager, client);
            case "play-card":
                return new PlayCardCommand(getPlayer(client), getGame(client));
            case "play-choose-color":
                return new PlayChooseColorCommand(getPlayer(client), getGame(client));
            case "play-plus-four":
                return new PlayPlusFourCommand(getPlayer(client), getGame(client));
            case "draw-card":
                return new DrawCardCommand(getPlayer(client), getGame(client));
            case "show-hand":
                return new ShowHandCommand(getPlayer(client), getGame(client));
            case "show-last-card":
                return new ShowLastCardCommand(getPlayer(client), getGame(client));
            case "show-played-cards":
                return new ShowPlayedCardsCommand(getPlayer(client), getGame(client));
            case "leave":
                return new LeaveCommand(getPlayer(client), getGame(client));
            default:
                throw new CommandNotFoundException("Unknown command: " + commandName);
        }
    }

    private Player getPlayer(SocketChannel client) {
        String username = userManager.getLoggedInUsername(client);
        if (username == null) {
            throw new IllegalStateException("Client is not logged in.");
        }

        Player player = userManager.getPlayerByUsername(username);
        if (player == null) {
            throw new IllegalStateException("Player not found for logged-in user: " + username);
        }

        return player;
    }

    private Game getGame(SocketChannel client) {
        Player player = getPlayer(client);

        Game game = player.getCurrentGame();
        if (game == null) {
            throw new IllegalStateException("Player " + player.getAccount().getUsername() + " is not part of any game.");
        }

        return game;
    }

}
