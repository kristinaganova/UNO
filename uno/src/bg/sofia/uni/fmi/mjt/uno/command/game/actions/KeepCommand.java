package bg.sofia.uni.fmi.mjt.uno.command.game.actions;

import bg.sofia.uni.fmi.mjt.uno.command.game.PlayerCommand;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.util.Optional;

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

        game.getGameMessenger().notifyAll("Player: " + player.getAccount().getUsername() + " drew a card");

        game.advanceTurn();

        return "You kept the card.";
    }

    private boolean validateLastCommandWasDrawCard() {
        return player.getHand().getLastDrawnCard().isPresent();
    }
}
