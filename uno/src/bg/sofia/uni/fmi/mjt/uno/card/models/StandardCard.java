package bg.sofia.uni.fmi.mjt.uno.card.models;

import bg.sofia.uni.fmi.mjt.uno.card.types.CardType;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.game.Game;

public final class StandardCard extends Card {
    private final int value;
    private static final int MAX_VALUE = 9;

    public StandardCard(Color color, int value) {
        if(color == Color.BLACK) {
            throw new IllegalArgumentException("There are no black standard cards.");
        }
        super(color, CardType.STANDARD);
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
    public boolean isPlayable(Card topCard) {
        return this.getColor() == topCard.getColor() ||
                (topCard instanceof StandardCard && this.value == ((StandardCard) topCard).value);
    }

    @Override
    public void applyEffect(Game game) {
    }
}
