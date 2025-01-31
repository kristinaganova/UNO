package bg.sofia.uni.fmi.mjt.uno.command.game.actions;

import bg.sofia.uni.fmi.mjt.uno.command.game.PlayerCommand;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.util.List;

public class KeepCommand extends PlayerCommand {

    public KeepCommand(Player player, Game game) {
        super(player, game);
    }

    @Override
    protected String executePlayerCommand(String[] args) {
        if (!validateLastCommandWasDrawCard()) {
            return "No drawn card to keep";
        }
        game.getGameMessenger().notifyAll("Player: " + player.getAccount().getUsername()
                + " choose to keep the drawn card");
        game.getTurnManager().advanceTurn();
        game.getTurnManager().announceTurn();
        return "You kept the card.";
    }

    private boolean validateLastCommandWasDrawCard() {
        List<String> recentCommands = game.getCommandLogger().getRecentCommands();
        if (recentCommands.isEmpty()) {
            return false;
        }

        String lastCommand = recentCommands.get(recentCommands.size() - 1);
        return lastCommand.contains("draw-card");
    }
}
