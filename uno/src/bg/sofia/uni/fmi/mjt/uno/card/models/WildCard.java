package bg.sofia.uni.fmi.mjt.uno.card.models;

import bg.sofia.uni.fmi.mjt.uno.card.strategy.CardEffectStrategy;
import bg.sofia.uni.fmi.mjt.uno.card.types.CardType;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.card.types.WildCardType;

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