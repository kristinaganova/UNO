package bg.sofia.uni.fmi.mjt.uno.server.command.game.actions;

import bg.sofia.uni.fmi.mjt.uno.server.command.game.PlayerCommand;
import bg.sofia.uni.fmi.mjt.uno.server.game.Game;
import bg.sofia.uni.fmi.mjt.uno.server.player.Player;

public class KeepCommand extends PlayerCommand {

    public KeepCommand(Player player, Game game) {
        super(player, game);
    }

    @Override
    protected String executePlayerCommand(String[] args) {
        if (!validateLastCommandWasDrawCard()) {
            return "No drawn card to keep";
        }

        player.getHand().setLastDrawnCard(null);

        game.getGameMessenger().notifyAll("Player: " + player.getAccount().username() + " drew a card");

        game.advanceTurn();

        return "You kept the card.";
    }

    private boolean validateLastCommandWasDrawCard() {
        return player.getHand().getLastDrawnCard().isPresent();
    }
}
