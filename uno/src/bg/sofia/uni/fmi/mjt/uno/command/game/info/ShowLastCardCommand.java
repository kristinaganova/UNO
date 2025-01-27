package bg.sofia.uni.fmi.mjt.uno.command.game.info;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.command.game.PlayerCommand;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

public class ShowLastCardCommand extends PlayerCommand {

    public ShowLastCardCommand(Player player, Game game) {
        super(player, game);
    }

    @Override
    protected String executePlayerCommand(String[] args) {
        Card lastCard = game.getDeckHandler().getDeck().getTopDiscardCard();
        if (lastCard == null) {
            return "No cards have been played yet.";
        }
        return "Last played card: " + lastCard.getCardDescription();
    }
}
