package bg.sofia.uni.fmi.mjt.uno.server.command.game.info;

import bg.sofia.uni.fmi.mjt.uno.server.command.game.PlayerCommand;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.Game;
import bg.sofia.uni.fmi.mjt.uno.server.player.Player;

public class SpectateCommand extends PlayerCommand {

    public SpectateCommand(Player player, Game game) {
        super(player, game);
    }

    @Override
    protected String executePlayerCommand(String[] args) {
        try {
            game.getPlayerRegistry().setPlayerAsSpectator(player);

            return "You are now spectating the game. The current state is:\n" + game.getSummary();
        } catch (IllegalStateException e) {
            return "Error: " + e.getMessage();
        }
    }
}
