package bg.sofia.uni.fmi.mjt.uno.card.models;

import bg.sofia.uni.fmi.mjt.uno.card.strategy.CardEffectStrategy;
import bg.sofia.uni.fmi.mjt.uno.card.types.CardType;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.game.Game;

public final class StandardCard extends Card {
    private final int value;
    private static final int MAX_VALUE = 9;

    public StandardCard(Color color, int value, CardEffectStrategy strategy) {
        if(color == Color.BLACK) {
            throw new IllegalArgumentException("There are no black standard cards.");
        }
        super(color, CardType.STANDARD, strategy);
        validateValue(value);
        this.value = value;
    }

    private void validateValue(int value) {
        if (value < 0 || value > MAX_VALUE) {
            throw new IllegalArgumentException("Card value must be between 0 and 9.");
        }
    }

    public int getValue() {
        return value;
    }

    @Override
    public String getCardDescription() {
        return getColor() + " " + value;
    }

    @Override
    public boolean isPlayable(Card topCard, Color currentColor) {
        if (topCard == null) {
            throw new IllegalArgumentException("Top card is null.");
        }
        return topCard.isPlayableWithStandard(this, currentColor);
    }

    @Override
    protected boolean isPlayableWithStandard(StandardCard other, Color currentColor) {
        return this.getColor() == other.getColor() || this.value == other.getValue();
    }

    @Override
    protected boolean isPlayableWithAction(ActionCard other, Color currentColor) {
        return this.getColor() == other.getColor();
    }

    @Override
    protected boolean isPlayableWithWild(WildCard other, Color currentColor) {
        return this.getColor() == currentColor;
    }
}
