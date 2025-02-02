package bg.sofia.uni.fmi.mjt.uno.server.command.game.actions;

import bg.sofia.uni.fmi.mjt.uno.server.command.game.PlayerCommand;
import bg.sofia.uni.fmi.mjt.uno.server.game.Game;
import bg.sofia.uni.fmi.mjt.uno.server.player.Player;

public class LeaveCommand extends PlayerCommand {

    public LeaveCommand(Player player, Game game) {
        super(player, game);
    }

    @Override
    protected String executePlayerCommand(String[] args) {
        return game.leaveGame(player);
    }
}
