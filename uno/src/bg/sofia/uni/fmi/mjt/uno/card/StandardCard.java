package bg.sofia.uni.fmi.mjt.uno.card;

public class StandardCard extends Card {
    private final int value;
    private static final int MAX_VALUE = 9;

    public StandardCard(Color color, int value) {
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
}
