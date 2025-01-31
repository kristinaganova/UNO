package bg.sofia.uni.fmi.mjt.uno.player;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.card.models.StandardCard;
import bg.sofia.uni.fmi.mjt.uno.card.strategy.StandardCardEffect;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HandTest {
    private Hand hand;
    private Card redCard;
    private Card blueCard;
    private Card greenCard;

    @BeforeEach
    void setUp() {
        hand = new Hand();

        redCard = new StandardCard(Color.RED, 1, new StandardCardEffect());
        blueCard = new StandardCard(Color.BLUE, 2, new StandardCardEffect());
        greenCard = new StandardCard(Color.GREEN, 3, new StandardCardEffect());

        hand.addCard(redCard);
        hand.addCard(blueCard);
    }

    @Test
    void testAddCardSuccessfully() {
        Map<Color, List<Card>> cards = hand.getHand();

        assertEquals(1, cards.get(Color.RED).size(), "Red card should be added to the hand.");
        assertEquals(redCard, cards.get(Color.RED).get(0), "Added red card should match.");

        assertEquals(1, cards.get(Color.BLUE).size(), "Blue card should be added to the hand.");
        assertEquals(blueCard, cards.get(Color.BLUE).get(0), "Added blue card should match.");
    }

    @Test
    void testAddNullCardThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> hand.addCard(null),
                "Adding null card should throw IllegalArgumentException.");
    }

    @Test
    void testRemoveCardSuccessfully() {
        assertTrue(hand.removeCard(redCard), "Removing an existing card should return true.");
        assertFalse(hand.getHand().get(Color.RED).contains(redCard),
                "Removed card should no longer be in the hand.");
    }

    @Test
    void testRemoveCardThatDoesNotExist() {
        assertFalse(hand.removeCard(greenCard), "Removing a non-existing card should return false.");
    }

    @Test
    void testGetCardByIdSuccessfully() {
        Card foundCard = hand.getCardById(redCard.getId());
        assertEquals(redCard, foundCard, "Card should be found by its ID.");
    }

    @Test
    void testGetCardByIdWithInvalidIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> hand.getCardById(null),
                "Getting a card with null ID should throw IllegalArgumentException.");
        assertThrows(IllegalArgumentException.class, () -> hand.getCardById(""),
                "Getting a card with blank ID should throw IllegalArgumentException.");
    }

    @Test
    void testGetSize() {
        assertEquals(2, hand.getSize(), "Hand should contain 2 cards.");
        hand.addCard(greenCard);
        assertEquals(3, hand.getSize(), "Hand should contain 3 cards after adding another.");
    }

    @Test
    void testRemoveAllCards() {
        hand.removeCards();
        assertEquals(0, hand.getSize(), "Hand should be empty after removing all cards.");
    }
}
