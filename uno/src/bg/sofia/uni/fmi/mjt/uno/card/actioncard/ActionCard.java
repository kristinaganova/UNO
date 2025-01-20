package bg.sofia.uni.fmi.mjt.uno.card.actioncard;

import bg.sofia.uni.fmi.mjt.uno.card.Card;
import bg.sofia.uni.fmi.mjt.uno.card.CardType;
import bg.sofia.uni.fmi.mjt.uno.card.Color;

public class ActionCard extends Card {
    private final ActionCardType type;

    public ActionCard(ActionCardType type, Color color) {
        super(color, CardType.ACTION);
        this.type = type;
    }

    public ActionCardType getType() {
        return type;
    }

    @Override
    public String getCardDescription() {
        return type.getDescription();
    }
}
