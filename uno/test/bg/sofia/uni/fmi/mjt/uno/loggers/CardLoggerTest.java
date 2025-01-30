package bg.sofia.uni.fmi.mjt.uno.loggers;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.card.models.StandardCard;
import bg.sofia.uni.fmi.mjt.uno.card.strategy.StandardCardEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardLoggerTest {
    private CardLogger cardLogger;
    private Card card1, card2, card3;

    @BeforeEach
    void setUp() {
        cardLogger = new CardLogger();
        card1 = new StandardCard(Color.RED, 3, new StandardCardEffect());
        card2 = new StandardCard(Color.BLUE, 5, new StandardCardEffect());
        card3 = new StandardCard(Color.GREEN, 7, new StandardCardEffect());
    }

    @Test
    void testLogCardSuccessfully() {
        cardLogger.logCard(card1);
        List<Card> playedCards = cardLogger.getPlayedCards();

        assertEquals(1, playedCards.size(), "Played cards should contain 1 card.");
        assertEquals(card1, playedCards.get(0), "Logged card should match the first card added.");
    }

    @Test
    void testLogCardMaxLimit() {
        for (int i = 0; i < 12; i++) {
            cardLogger.logCard(new StandardCard(Color.YELLOW, i % 10, new StandardCardEffect()));
        }

        List<Card> playedCards = cardLogger.getPlayedCards();
        assertEquals(10, playedCards.size(), "Played cards should not exceed 10.");
    }

    @Test
    void testLogCardNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> cardLogger.logCard(null), "Logging null should throw an exception.");
    }

    @Test
    void testGetLogSummary() {
        cardLogger.logCard(card1);
        cardLogger.logCard(card2);
        cardLogger.logCard(card3);

        String logSummary = cardLogger.getLogSummary();
        assertTrue(logSummary.contains(card1.getCardDescription()), "Summary should contain the first card.");
        assertTrue(logSummary.contains(card2.getCardDescription()), "Summary should contain the second card.");
        assertTrue(logSummary.contains(card3.getCardDescription()), "Summary should contain the third card.");
    }
}
