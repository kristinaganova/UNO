package bg.sofia.uni.fmi.mjt.uno.card.models;

import bg.sofia.uni.fmi.mjt.uno.card.strategy.CardEffectStrategy;
import bg.sofia.uni.fmi.mjt.uno.card.types.CardType;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.card.types.WildCardType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WildCardTest {
    private WildCard wildCard;
    private CardEffectStrategy effectStrategy;

    @BeforeEach
    void setUp() {
        effectStrategy = mock(CardEffectStrategy.class);
        wildCard = new WildCard(WildCardType.PLUS_FOUR, effectStrategy);
    }

    @Test
    void testConstructorValidWildCard() {
        assertNotNull(wildCard, "WildCard object should not be null.");
        assertEquals(Color.BLACK, wildCard.getColor(), "Wild cards should always have BLACK as color.");
        assertEquals(CardType.WILD, wildCard.getCardType(), "Wild card should have type WILD.");
        assertEquals(WildCardType.PLUS_FOUR, wildCard.getWildCardType(), "Wild card type should be WILD_DRAW_FOUR.");
    }

    @Test
    void testGetCardDescription() {
        assertEquals("Makes the next player draw 4 cards and lets you pick a color BLACK", wildCard.getCardDescription(),
                "Card description should match WildCardType.");
    }

    @Test
    void testIsPlayableWithStandardCard() {
        StandardCard standardCard = new StandardCard(Color.RED, 5, effectStrategy);
        assertTrue(wildCard.isPlayable(standardCard, Color.RED),
                "Wild cards should be playable with any standard card.");
    }

    @Test
    void testIsPlayableWithActionCard() {
        ActionCard actionCard = new ActionCard(Color.GREEN, bg.sofia.uni.fmi.mjt.uno.card.types.ActionCardType.SKIP, effectStrategy);
        assertTrue(wildCard.isPlayable(actionCard, Color.GREEN),
                "Wild cards should be playable with any action card.");
    }

    @Test
    void testIsPlayableWithAnotherWildCard() {
        WildCard anotherWildCard = new WildCard(WildCardType.PLUS_FOUR, effectStrategy);
        assertTrue(wildCard.isPlayable(anotherWildCard, Color.BLUE),
                "Wild cards should be playable with any other wild card.");
    }

    @Test
    void testIsPlayableReturnsTrueForAnyCurrentColor() {
        StandardCard standardCard = new StandardCard(Color.YELLOW, 2, effectStrategy);
        assertTrue(wildCard.isPlayable(standardCard, Color.YELLOW),
                "Wild cards should be playable regardless of the current color.");
    }

    @Test
    void testIsPlayableThrowsExceptionForNullTopCard() {
        assertThrows(IllegalArgumentException.class,
                () -> wildCard.isPlayable(null, Color.RED),
                "Should throw an exception if the top card is null.");
    }

    @Test
    void testGetWildCardType() {
        assertEquals(WildCardType.PLUS_FOUR, wildCard.getWildCardType(),
                "WildCard type should match the one provided in the constructor.");
    }
}
