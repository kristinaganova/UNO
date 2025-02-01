package bg.sofia.uni.fmi.mjt.uno.command.game.actions;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.command.game.PlayerCommand;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

public class DrawCardCommand extends PlayerCommand {

    public DrawCardCommand(Player player, Game game) {
        super(player, game);
    }

    @Override
    protected String executePlayerCommand(String[] args) {
        validatePlayerTurn();

        Card drawnCard = game.getDeckHandler().drawCard(player);
        notifyCardDrawn(drawnCard);

        if (isCardPlayable(drawnCard)) {
            return promptPlayOrKeep(drawnCard);
        }
        return keepDrawnCard(drawnCard);
    }

    private void notifyCardDrawn(Card drawnCard) {
        game.getGameMessenger().notifyAll("Player: " + player.getAccount().getUsername() + " drew a card.");
    }

    private boolean isCardPlayable(Card drawnCard) {
        Card topCard = game.getDeckHandler().getDeck().getTopDiscardCard();
        return drawnCard.isPlayable(topCard, game.getDeckHandler().getCurrentColor());
    }

    private String promptPlayOrKeep(Card drawnCard) {
        String respose = "You drew: " + drawnCard.getCardDescription() +
                        " (ID: " + drawnCard.getId() + ")." + System.lineSeparator() +
                        "You can either:" + System.lineSeparator() +
                        "1. Play it: card-id=" + drawnCard.getId() + System.lineSeparator() +
                        "2. Keep it: keep";
        return respose;
    }

    private String keepDrawnCard(Card drawnCard) {
        game.advanceTurn();
        player.getHand().addDrawnCardToHand();
        return "You drew: " + drawnCard.getCardDescription() + " and kept it.";
    }
}
