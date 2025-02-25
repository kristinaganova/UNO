package bg.sofia.uni.fmi.mjt.uno.server.games.game.components;

import bg.sofia.uni.fmi.mjt.uno.server.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.server.card.models.StandardCard;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.server.deck.UnoDeck;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.components.DeckHandler;
import bg.sofia.uni.fmi.mjt.uno.server.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeckHandlerTest {

    private DeckHandler deckHandler;
    private UnoDeck mockDeck;
    private Player mockPlayer;
    private Card mockCard;

    @BeforeEach
    void setUp() {
        mockDeck = mock(UnoDeck.class);
        mockPlayer = mock(Player.class);
        mockCard = mock(StandardCard.class);

        deckHandler = new DeckHandler();
    }

    @Test
    void testDrawCard() {
        when(mockDeck.drawCard()).thenReturn(mockCard);
        when(mockCard.getCardDescription()).thenReturn("Standard Card Red 5");

        Card drawnCard = deckHandler.drawCard(mockPlayer);

        assertNotNull(drawnCard, "Drawn card should not be null.");
        verify(mockPlayer, times(1)).addCardToHand(drawnCard);
    }

    @Test
    void testDrawCardThrowsExceptionForNullPlayer() {
        assertThrows(IllegalArgumentException.class, () -> deckHandler.drawCard(null),
                "Drawing a card should throw IllegalArgumentException when the player is null.");
    }

    @Test
    void testDiscardCard() {
        when(mockCard.getColor()).thenReturn(Color.BLUE);

        deckHandler.discardCard(mockCard);

        assertEquals(Color.BLUE, deckHandler.getCurrentColor(), "The current color should be set to the discarded card's color.");
    }

    @Test
    void testDiscardCardThrowsExceptionForNullCard() {
        assertThrows(IllegalArgumentException.class, () -> deckHandler.discardCard(null),
                "Discarding a null card should throw IllegalArgumentException.");
    }

    @Test
    void testGetTopDiscardCard() {
        when(mockDeck.getTopDiscardCard()).thenReturn(mockCard);
        when(mockCard.getCardType()).thenReturn(null); // Ensure it's not wild

        Card topCard = deckHandler.getTopDiscardCard();
        assertNotNull(topCard, "Top discard card should not be null.");
    }

    @Test
    void testGetCurrentColor() {
        deckHandler.setCurrentColor(Color.GREEN);
        assertEquals(Color.GREEN, deckHandler.getCurrentColor(), "Current color should be GREEN.");
    }

    @Test
    void testSetCurrentColorThrowsExceptionForNull() {
        assertThrows(IllegalArgumentException.class, () -> deckHandler.setCurrentColor(null),
                "Setting a null color should throw IllegalArgumentException.");
    }
}
