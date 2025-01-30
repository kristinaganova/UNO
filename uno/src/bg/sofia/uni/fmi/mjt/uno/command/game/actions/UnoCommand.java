package bg.sofia.uni.fmi.mjt.uno.command.game.actions;

import bg.sofia.uni.fmi.mjt.uno.command.game.PlayerCommand;
import bg.sofia.uni.fmi.mjt.uno.exceptions.command.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

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
        game.getGameMessenger().notifyAll(player.getAccount().getUsername() + " has declared UNO!");
        return "You have declared UNO!";
    }
}
