package bg.sofia.uni.fmi.mjt.uno.card.models;

import bg.sofia.uni.fmi.mjt.uno.card.types.CardType;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.game.Game;

public abstract sealed class Card permits ActionCard, StandardCard, WildCard {
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

    public abstract void applyEffect(Game game);

    public abstract boolean isPlayable(Card topCard);
}