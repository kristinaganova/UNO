package bg.sofia.uni.fmi.mjt.uno.server.command.game.actions;

import bg.sofia.uni.fmi.mjt.uno.server.command.game.PlayerCommand;
import bg.sofia.uni.fmi.mjt.uno.server.exceptions.command.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.Game;
import bg.sofia.uni.fmi.mjt.uno.server.player.Player;

public class UnoCommand extends PlayerCommand {

    public UnoCommand(Player player, Game game) {
        super(player, game);
    }

    @Override
    protected String executePlayerCommand(String[] args) {
        if (player.getHandSize() != 1) {
            throw new CommandExecutionException("You can only call UNO when you have exactly one card left!");
        }

        player.callUno();
        game.getGameMessenger().notifyAll(player.getAccount().username() + " has declared UNO!");
        return "You have declared UNO!";
    }
}
