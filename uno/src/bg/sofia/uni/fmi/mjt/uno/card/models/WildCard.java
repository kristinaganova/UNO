package bg.sofia.uni.fmi.mjt.uno.card.models;

import bg.sofia.uni.fmi.mjt.uno.card.strategy.CardEffectStrategy;
import bg.sofia.uni.fmi.mjt.uno.card.types.CardType;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.card.types.WildCardType;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

public non-sealed class WildCard extends Card {

    private final WildCardType type;

    private static final int CARDS_TO_DRAW = 4;

    public WildCard(WildCardType type, CardEffectStrategy strategy) {
        super(Color.BLACK, CardType.WILD, strategy);
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

    public WildCardType getWildCardType() {
        return type;
    }
}