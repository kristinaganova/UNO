package bg.sofia.uni.fmi.mjt.uno.card.models;

import bg.sofia.uni.fmi.mjt.uno.card.strategy.CardEffectStrategy;
import bg.sofia.uni.fmi.mjt.uno.card.types.CardType;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StandardCardTest {
    private StandardCard standardCard;
    private CardEffectStrategy effectStrategy;

    @BeforeEach
    void setUp() {
        effectStrategy = mock(CardEffectStrategy.class);
        standardCard = new StandardCard(Color.RED, 5, effectStrategy);
    }

    @Test
    void testConstructorValidCard() {
        assertNotNull(standardCard, "StandardCard object should not be null.");
        assertEquals(Color.RED, standardCard.getColor(), "Card color should be RED.");
        assertEquals(CardType.STANDARD, standardCard.getCardType(), "Card type should be STANDARD.");
        assertEquals(5, standardCard.getValue(), "Card value should be 5.");
    }

    @Test
    void testConstructorThrowsExceptionForBlackCard() {
        assertThrows(IllegalArgumentException.class,
                () -> new StandardCard(Color.BLACK, 5, effectStrategy),
                "Should throw an exception for black standard cards.");
    }

    @Test
    void testConstructorThrowsExceptionForInvalidValue() {
        assertThrows(IllegalArgumentException.class,
                () -> new StandardCard(Color.RED, 10, effectStrategy),
                "Should throw an exception for invalid card value (above 9).");
        assertThrows(IllegalArgumentException.class,
                () -> new StandardCard(Color.RED, -1, effectStrategy),
                "Should throw an exception for invalid card value (negative).");
    }

    @Test
    void testGetCardDescription() {
        assertEquals("RED 5", standardCard.getCardDescription(), "Card description should match.");
    }

    @Test
    void testIsPlayableWithSameColorStandardCard() {
        StandardCard otherCard = new StandardCard(Color.RED, 3, effectStrategy);
        assertTrue(standardCard.isPlayable(otherCard, Color.RED),
                "Should be playable with another standard card of the same color.");
    }

    @Test
    void testIsPlayableWithSameValueStandardCard() {
        StandardCard otherCard = new StandardCard(Color.BLUE, 5, effectStrategy);
        assertTrue(standardCard.isPlayable(otherCard, Color.RED),
                "Should be playable with another standard card of the same value.");
    }

    @Test
    void testIsNotPlayableWithDifferentColorAndValueStandardCard() {
        StandardCard otherCard = new StandardCard(Color.BLUE, 7, effectStrategy);
        assertFalse(standardCard.isPlayable(otherCard, Color.RED),
                "Should not be playable with a different color and value standard card.");
    }

    @Test
    void testIsPlayableWithSameColorActionCard() {
        ActionCard actionCard = new ActionCard(Color.RED, bg.sofia.uni.fmi.mjt.uno.card.types.ActionCardType.SKIP, effectStrategy);
        assertTrue(standardCard.isPlayable(actionCard, Color.RED),
                "Should be playable with an action card of the same color.");
    }

    @Test
    void testIsNotPlayableWithDifferentColorActionCard() {
        ActionCard actionCard = new ActionCard(Color.BLUE, bg.sofia.uni.fmi.mjt.uno.card.types.ActionCardType.SKIP, effectStrategy);
        assertFalse(standardCard.isPlayable(actionCard, Color.RED),
                "Should not be playable with an action card of a different color.");
    }


    @Test
    void testIsPlayableThrowsExceptionForNullTopCard() {
        assertThrows(IllegalArgumentException.class,
                () -> standardCard.isPlayable(null, Color.RED),
                "Should throw an exception if the top card is null.");
    }
}
