package bg.sofia.uni.fmi.mjt.uno.card.models;

import bg.sofia.uni.fmi.mjt.uno.card.types.CardType;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.card.types.WildCardType;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

public non-sealed class WildCard extends Card {

    private final WildCardType type;
    public WildCard(WildCardType type) {
        super(Color.BLACK, CardType.WILD);
        this.type = type;
    }

    @Override
    public String getCardDescription() {
        return type.getDescription();
    }

    @Override
    public boolean isPlayable(Card topCard) {
        return true;
    }

    private static final int PLUS_FOUR = 4;

    @Override
    public void applyEffect(Game game) {
        Color chosenColor = game.getTurnManager().getCurrentPlayer().chooseColor();
        game.setCurrentColor(chosenColor);

        if (type == WildCardType.PLUS_FOUR) {
            Player nextPlayer = game.getTurnManager().getNextPlayer();
            for (int i = 0; i < PLUS_FOUR; i++) {
                game.drawCard(nextPlayer);
            }
            game.getTurnManager().skipTurn();
        }
    }
}
