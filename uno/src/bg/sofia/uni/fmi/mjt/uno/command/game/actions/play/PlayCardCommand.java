package bg.sofia.uni.fmi.mjt.uno.command.game.actions.play;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.command.game.PlayerCommand;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

public class PlayCardCommand extends PlayerCommand {

    public PlayCardCommand(Player player, Game game) {
        super(player, game);
    }

    @Override
    protected String executePlayerCommand(String[] args) {

        validatePlayerTurn();

        if (args == null || args.length < 1) {
            throw new IllegalArgumentException("Usage: play-card --card-id=<card-id>");
        }

        String cardId = getArgumentValue(args, "--card-id");
        Card cardToPlay = findCardById(cardId);
        Card topCard = game.getDeckHandler().getDeck().getTopDiscardCard();

        if (cardToPlay.getColor() == Color.BLACK) {
            throw new IllegalArgumentException("Black cards have other commands.");
        }

        if (!cardToPlay.isPlayable(topCard, game.getDeckHandler().getCurrentColor())) {
            throw new IllegalArgumentException("The selected card cannot be played on the current top card.");
        }

        playCard(cardToPlay, null);

        return "You played " + cardToPlay.getCardDescription() + ".";

    }
}
