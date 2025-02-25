package bg.sofia.uni.fmi.mjt.uno.server.card.models;

import bg.sofia.uni.fmi.mjt.uno.server.card.models.ActionCard;
import bg.sofia.uni.fmi.mjt.uno.server.card.models.StandardCard;
import bg.sofia.uni.fmi.mjt.uno.server.card.models.WildCard;
import bg.sofia.uni.fmi.mjt.uno.server.card.strategy.CardEffectStrategy;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.CardType;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.WildCardType;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.ActionCardType;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.Game;
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
        assertEquals("Makes the next player draw 4 cards and lets you pick a color BLACK",
                wildCard.getCardDescription(),
                "Card description should match WildCardType.");
    }

    @Test
    void testIsPlayableWithStandardCardMatchingColor() {
        StandardCard standardCard = new StandardCard(Color.RED, 5, effectStrategy);
        assertTrue(wildCard.isPlayable(standardCard, Color.RED),
                "Wild cards should be playable if the current color matches.");
    }

    @Test
    void testIsPlayableWithStandardCardDifferentColor() {
        StandardCard standardCard = new StandardCard(Color.YELLOW, 3, effectStrategy);
        assertFalse(wildCard.isPlayable(standardCard, Color.RED),
                "Wild cards should not be playable if the current color does not match.");
    }

    @Test
    void testIsPlayableWithActionCardMatchingColor() {
        ActionCard actionCard = new ActionCard(Color.GREEN, ActionCardType.SKIP, effectStrategy);
        assertTrue(wildCard.isPlayable(actionCard, Color.GREEN),
                "Wild cards should be playable if the current color matches.");
    }

    @Test
    void testIsPlayableWithActionCardDifferentColor() {
        ActionCard actionCard = new ActionCard(Color.BLUE, ActionCardType.REVERSE, effectStrategy);
        assertFalse(wildCard.isPlayable(actionCard, Color.YELLOW),
                "Wild cards should not be playable if the current color does not match.");
    }

    @Test
    void testIsPlayableWithAnotherWildCard() {
        WildCard anotherWildCard = new WildCard(WildCardType.PLUS_FOUR, effectStrategy);
        assertTrue(wildCard.isPlayable(anotherWildCard, Color.BLUE),
                "Wild cards should be playable with any other wild card.");
    }

    @Test
    void testIsPlayableWithDifferentWildCardType() {
        WildCard anotherWildCard = new WildCard(WildCardType.PLUS_FOUR, effectStrategy);
        assertTrue(wildCard.isPlayable(anotherWildCard, Color.GREEN),
                "Different wild card types should still be playable with each other.");
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

    @Test
    void testEffectStrategyIsCalled() {
        wildCard.applyEffect(mock(Game.class));
        verify(effectStrategy, times(1)).applyEffect(any());
    }
}
