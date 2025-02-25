package bg.sofia.uni.fmi.mjt.uno.server.command.game.info;

import bg.sofia.uni.fmi.mjt.uno.server.command.game.PlayerCommand;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.Game;
import bg.sofia.uni.fmi.mjt.uno.server.player.Player;

public class ShowHandCommand extends PlayerCommand {

    public ShowHandCommand(Player player, Game game) {
        super(player, game);
    }

    @Override
    protected String executePlayerCommand(String[] args) {
        return player.showHand();
    }
}