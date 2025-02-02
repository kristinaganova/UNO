package bg.sofia.uni.fmi.mjt.uno.server.card.models;

import bg.sofia.uni.fmi.mjt.uno.server.card.models.ActionCard;
import bg.sofia.uni.fmi.mjt.uno.server.card.models.StandardCard;
import bg.sofia.uni.fmi.mjt.uno.server.card.strategy.CardEffectStrategy;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.ActionCardType;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.CardType;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActionCardTest {
    private ActionCard actionCard;
    private CardEffectStrategy effectStrategy;

    @BeforeEach
    void setUp() {
        effectStrategy = mock(CardEffectStrategy.class);
        actionCard = new ActionCard(Color.RED, ActionCardType.SKIP, effectStrategy);
    }

    @Test
    void testConstructor() {
        assertNotNull(actionCard, "ActionCard object should not be null.");
        assertEquals(Color.RED, actionCard.getColor(), "Card color should be RED.");
        assertEquals(CardType.ACTION, actionCard.getCardType(), "Card type should be ACTION.");
        assertEquals(ActionCardType.SKIP, actionCard.getType(), "Action card type should be SKIP.");
    }

    @Test
    void testGetCardDescription() {
        String expectedDescription = "Skips the next player's turn RED";
        assertEquals(expectedDescription, actionCard.getCardDescription(), "Card description does not match.");
    }

    @Test
    void testIsPlayableWithSameColorStandardCard() {
        StandardCard standardCard = new StandardCard(Color.RED, 5, effectStrategy);
        assertTrue(actionCard.isPlayable(standardCard, Color.RED), "Should be playable with a standard card of the same color.");
    }

    @Test
    void testIsPlayableWithDifferentColorStandardCard() {
        StandardCard standardCard = new StandardCard(Color.BLUE, 5, effectStrategy);
        assertFalse(actionCard.isPlayable(standardCard, Color.RED), "Should not be playable with a standard card of a different color.");
    }

    @Test
    void testIsPlayableWithSameColorActionCard() {
        ActionCard sameColorActionCard = new ActionCard(Color.RED, ActionCardType.REVERSE, effectStrategy);
        assertTrue(actionCard.isPlayable(sameColorActionCard, Color.RED), "Should be playable with an action card of the same color.");
    }

    @Test
    void testIsPlayableWithSameTypeActionCard() {
        ActionCard sameTypeActionCard = new ActionCard(Color.BLUE, ActionCardType.SKIP, effectStrategy);
        assertTrue(actionCard.isPlayable(sameTypeActionCard, Color.RED), "Should be playable with an action card of the same type.");
    }

    @Test
    void testIsPlayableWithDifferentColorActionCard() {
        ActionCard differentColorActionCard = new ActionCard(Color.BLUE, ActionCardType.REVERSE, effectStrategy);
        assertFalse(actionCard.isPlayable(differentColorActionCard, Color.RED), "Should not be playable with an action card of a different color and type.");
    }

    @Test
    void testIsPlayableWithNullCard() {
        assertFalse(actionCard.isPlayable(null, Color.RED), "Should return false when checking playability with a null card.");
    }

    @Test
    void testIsPlayableWithNullCurrentColor() {
        StandardCard standardCard = new StandardCard(Color.RED, 5, effectStrategy);
        assertFalse(actionCard.isPlayable(standardCard, null), "Should return false when checking playability with a null current color.");
    }
}
