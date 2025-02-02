package bg.sofia.uni.fmi.mjt.uno.server.deck;

import bg.sofia.uni.fmi.mjt.uno.server.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.server.card.models.StandardCard;
import bg.sofia.uni.fmi.mjt.uno.server.card.models.WildCard;
import bg.sofia.uni.fmi.mjt.uno.server.card.strategy.PickColorEffect;
import bg.sofia.uni.fmi.mjt.uno.server.card.strategy.PlusFourEffect;
import bg.sofia.uni.fmi.mjt.uno.server.card.strategy.StandardCardEffect;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.WildCardType;
import bg.sofia.uni.fmi.mjt.uno.server.deck.UnoDeck;
import bg.sofia.uni.fmi.mjt.uno.server.exceptions.deck.IllegalDeckSateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnoDeckTest {

    private UnoDeck unoDeck;

    @BeforeEach
    void setUp() {
        unoDeck = new UnoDeck();
    }

    @Test
    void testDeckInitialization_HasCards() {
        assertFalse(unoDeck.getRemainingCards() == 0, "Deck should not be empty after initialization.");
    }

    @Test
    void testShuffleDeck_DoesNotThrow() {
        assertDoesNotThrow(() -> unoDeck.shuffleDeck());
    }

    @Test
    void testDrawCard_ValidDraw() {
        Card drawnCard = unoDeck.drawCard();
        assertNotNull(drawnCard, "Drawn card should not be null.");
    }

    @Test
    void testDrawCard_ThrowsExceptionWhenEmpty() {
        while (unoDeck.getRemainingCards() > 0) {
            unoDeck.drawCard();
        }
        assertThrows(IllegalDeckSateException.class, () -> unoDeck.drawCard());
    }

    @Test
    void testDiscardCard_AddsCardToDiscardPile() {
        Card card = new WildCard(WildCardType.PICK_COLOR, new PickColorEffect());
        unoDeck.discardCard(card);

        assertEquals(card, unoDeck.getTopDiscardCard(), "Discarded card should be on top of the discard pile.");
    }

    @Test
    void testDiscardCard_NullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> unoDeck.discardCard(null));
    }

    @Test
    void testReplenishDeckFromDiscardPile() {
        Card topCard = new WildCard(WildCardType.PLUS_FOUR, new PlusFourEffect());
        unoDeck.discardCard(new StandardCard(Color.RED, 5, new StandardCardEffect()));
        unoDeck.discardCard(topCard);

        while (unoDeck.getRemainingCards() > 0) {
            unoDeck.drawCard();
        }

        unoDeck.replenishDrawPile();

        assertTrue(unoDeck.getRemainingCards() > 0, "Deck should have been replenished from discard pile.");
    }
}
