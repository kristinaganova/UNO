package bg.sofia.uni.fmi.mjt.uno.command.game.play;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.command.game.PlayerCommand;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

public class PlayCardCommand extends PlayerCommand {

    public PlayCardCommand(Player player, Game game) {
        super(player, game);
    }

    @Override
    protected String executePlayerCommand(String[] args) {
        if (args == null || args.length < 1) {
            throw new IllegalArgumentException("Usage: play-card --card-id=<card-id>");
        }

        String cardId = getArgumentValue(args, "--card-id");

        Card cardToPlay = findCardById(cardId);

        Card topCard = game.getDeck().getTopDiscardCard();
        if (!cardToPlay.isPlayable(topCard, game.getCurrentColor())) {
            throw new IllegalArgumentException("The selected card cannot be played on the current top card.");
        }

        player.removeCardFromHand(cardToPlay);
        game.getDeck().discardCard(cardToPlay);

        cardToPlay.applyEffect(game);

        return "You played " + cardToPlay.getCardDescription() + ".";
    }

    private String getArgumentValue(String[] args, String key) {
        for (String arg : args) {
            if (arg.startsWith(key + "=")) {
                return arg.substring((key + "=").length());
            }
        }
        throw new IllegalArgumentException("Missing argument: " + key);
    }

    private Card findCardById(String cardId) {
        for (Card card : player.getHandManager().getAllCards()) {
            if (card.getId().equals(cardId)) {
                return card;
            }
        }
        throw new IllegalArgumentException("Card with ID " + cardId + " does not exist in your hand.");
    }
}
