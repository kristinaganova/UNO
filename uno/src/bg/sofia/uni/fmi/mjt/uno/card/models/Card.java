package bg.sofia.uni.fmi.mjt.uno.card.models;

import bg.sofia.uni.fmi.mjt.uno.card.strategy.CardEffectStrategy;
import bg.sofia.uni.fmi.mjt.uno.card.types.CardType;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.game.Game;

import java.util.UUID;

public abstract sealed class Card permits ActionCard, StandardCard, WildCard {
    private final String id;
    private final Color color;
    private final CardType type;
    private final CardEffectStrategy effectStrategy;

    public Card(Color color, CardType type, CardEffectStrategy effectStrategy) {
        this.id = UUID.randomUUID().toString();
        this.color = color;
        this.type = type;
        this.effectStrategy = effectStrategy;
    }

    public Color getColor() {
        return color;
    }

    public CardType getCardType() {
        return type;
    }

    public void applyEffect(Game game) {
        effectStrategy.applyEffect(game);
    }

    public String getId() {
        return id;
    }

    public abstract String getCardDescription();

    public abstract boolean isPlayable(Card topCard, Color currentColor);

    public abstract boolean isPlayableWithStandard(StandardCard other, Color currentColor);

    public abstract boolean isPlayableWithAction(ActionCard other, Color currentColor);

    public abstract boolean isPlayableWithWild(WildCard other, Color currentColor);

}