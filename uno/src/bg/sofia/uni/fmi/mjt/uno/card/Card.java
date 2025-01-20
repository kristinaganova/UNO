package bg.sofia.uni.fmi.mjt.uno.card;

public abstract class Card {
    private final Color color;
    private final CardType type;

    public Card(Color color, CardType type) {
        this.color = color;
        this.type = type;
    }

    public Color getColor() {
        return color;
    }

    public CardType getCardType() {
        return type;
    }

    public abstract String getCardDescription();
}