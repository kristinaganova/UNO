package bg.sofia.uni.fmi.mjt.uno.card.models;

import bg.sofia.uni.fmi.mjt.uno.card.strategy.CardEffectStrategy;
import bg.sofia.uni.fmi.mjt.uno.card.types.CardType;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.exceptions.card.CardException;
import bg.sofia.uni.fmi.mjt.uno.game.Game;

import java.util.UUID;

public abstract sealed class Card permits ActionCard, StandardCard, WildCard {
    private final String id;
    private final Color color;
    private final CardType type;
    private final CardEffectStrategy effectStrategy;

    public Card(Color color, CardType type, CardEffectStrategy effectStrategy) {
        if (color == null) {
            throw new CardException("color cannot be null");
        }
        if (type == null) {
            throw new CardException("type cannot be null");
        }
        if (effectStrategy == null) {
            throw new CardException("effectStrategy cannot be null");
        }

        this.id = UUID.randomUUID().toString().substring(0, 5);
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

    protected abstract boolean isPlayableWithStandard(StandardCard other, Color currentColor);

    protected abstract boolean isPlayableWithAction(ActionCard other, Color currentColor);

    protected abstract boolean isPlayableWithWild(WildCard other, Color currentColor);

}