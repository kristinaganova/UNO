package bg.sofia.uni.fmi.mjt.uno.card.models;

import bg.sofia.uni.fmi.mjt.uno.card.types.CardType;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.card.types.WildCardType;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

public non-sealed class WildCard extends Card {

    private final WildCardType type;

    private static final int CARDS_TO_DRAW = 4;

    public WildCard(WildCardType type) {
        super(Color.BLACK, CardType.WILD);
        this.type = type;
    }

    @Override
    public String getCardDescription() {
        return type.getDescription() + " " + getColor();
    }

    @Override
    public boolean isPlayable(Card topCard, Color currentColor) {
        return topCard.isPlayableWithWild(this, currentColor);
    }

    @Override
    public boolean isPlayableWithStandard(StandardCard other, Color currentColor) {
        return true;
    }

    @Override
    public boolean isPlayableWithAction(ActionCard other, Color currentColor) {
        return true;
    }

    @Override
    public boolean isPlayableWithWild(WildCard other, Color currentColor) {
        return true;
    }

    @Override
    public void applyEffect(Game game) {
        Player currentPlayer = game.getTurnManager().getCurrentPlayer();
        String chosenColor = null;

        while (chosenColor == null) {
            chosenColor = game.promptPlayerToChooseColor(currentPlayer);

            if (chosenColor == null) {
                currentPlayer.sendMessage("You took too long. Please try again.");
            }
        }

        game.setCurrentColor(Color.valueOf(chosenColor.toUpperCase()));
        game.notifyPlayers(currentPlayer.getAccount().getUsername() + " chose " + chosenColor + " as the new color.");

        if (type == WildCardType.PLUS_FOUR) {
            Player nextPlayer = game.getTurnManager().getNextPlayer();
            for (int i = 0; i < CARDS_TO_DRAW; i++) {
                game.drawCard(nextPlayer);
            }
            game.notifyPlayers(nextPlayer.getAccount().getUsername() + " drew " + CARDS_TO_DRAW + " cards.");
        }

        game.getTurnManager().advanceTurn();
    }

    public WildCardType getWildCardType() {
        return type;
    }
}