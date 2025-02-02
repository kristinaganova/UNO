package bg.sofia.uni.fmi.mjt.uno.server.card.models;

import bg.sofia.uni.fmi.mjt.uno.server.card.strategy.CardEffectStrategy;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.ActionCardType;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.CardType;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.Color;

public non-sealed class ActionCard extends Card {
    private final ActionCardType type;

    public ActionCard(Color color, ActionCardType type, CardEffectStrategy effectStrategy) {
        super(color, CardType.ACTION, effectStrategy);
        this.type = type;
    }

    public ActionCardType getType() {
        return type;
    }

    @Override
    public String getCardDescription() {
        return type.getDescription() + " " + getColor().toString();
    }

    @Override
    public boolean isPlayable(Card other, Color currentColor) {
        if (other == null || currentColor == null) {
            return false;
        }
        return other.isPlayableWithAction(this, currentColor);
    }

    @Override
    protected boolean isPlayableWithStandard(StandardCard other, Color currentColor) {
        return this.getColor() == other.getColor();
    }

    @Override
    protected boolean isPlayableWithAction(ActionCard other, Color currentColor) {
        return this.getColor() == other.getColor() || this.type == other.type;
    }

    @Override
    protected boolean isPlayableWithWild(WildCard other, Color currentColor) {
        return this.getColor() == currentColor;
    }

}
