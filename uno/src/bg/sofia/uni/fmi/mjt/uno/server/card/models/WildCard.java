package bg.sofia.uni.fmi.mjt.uno.server.card.models;

import bg.sofia.uni.fmi.mjt.uno.server.card.strategy.CardEffectStrategy;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.CardType;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.WildCardType;

public non-sealed class WildCard extends Card {

    private final WildCardType type;

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
        if (topCard == null) {
            throw new IllegalArgumentException("Top card is null.");
        }
        return topCard.isPlayableWithWild(this, currentColor);
    }

    @Override
    protected boolean isPlayableWithStandard(StandardCard other, Color currentColor) {
        return other.getColor() == currentColor;
    }

    @Override
    protected boolean isPlayableWithAction(ActionCard other, Color currentColor) {
        return other.getColor() == currentColor;
    }

    @Override
    protected boolean isPlayableWithWild(WildCard other, Color currentColor) {
        return true;
    }

    public WildCardType getWildCardType() {
        return type;
    }
}