package bg.sofia.uni.fmi.mjt.uno.card.factory;

import bg.sofia.uni.fmi.mjt.uno.card.models.ActionCard;
import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.card.models.StandardCard;
import bg.sofia.uni.fmi.mjt.uno.card.models.WildCard;
import bg.sofia.uni.fmi.mjt.uno.card.types.ActionCardType;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.card.types.WildCardType;

public class CardFactory {
    public static Card createActionCard(Color color, ActionCardType type) {
        return new ActionCard(type, color);
    }

    public static Card createStandardCard(Color color, int value) {
        return new StandardCard(color, value);
    }

    public static Card createWildCard(WildCardType type) {
        return new WildCard(type);
    }
}
