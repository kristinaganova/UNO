package bg.sofia.uni.fmi.mjt.uno.command.game.actions;

import bg.sofia.uni.fmi.mjt.uno.command.game.PlayerCommand;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

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
                + " chose to keep the drawn card.");

        player.getHand().addDrawnCardToHand();
        game.advanceTurn();
        return "You kept the card.";
    }

    private boolean validateLastCommandWasDrawCard() {
        return !player.getHand().getLastDrawnCard().isEmpty();
    }
}
