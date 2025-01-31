package bg.sofia.uni.fmi.mjt.uno.player;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.card.models.StandardCard;
import bg.sofia.uni.fmi.mjt.uno.card.strategy.StandardCardEffect;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.account.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerTest {
    private Player player;
    private Account mockAccount;
    private SocketChannel mockClient;
    private Card redCard;
    private Card blueCard;
    private Game mockGame;

    @BeforeEach
    void setUp() {
        mockAccount = mock(Account.class);
        mockClient = mock(SocketChannel.class);
        mockGame = mock(Game.class);

        player = new Player(mockAccount, mockClient);

        redCard = new StandardCard(Color.RED, 1, new StandardCardEffect());
        blueCard = new StandardCard(Color.BLUE, 2, new StandardCardEffect());
    }

    @Test
    void testAddCardToHand() {
        player.addCardToHand(redCard);
        assertEquals(1, player.getHandSize(), "Player's hand should contain 1 card.");
    }

    @Test
    void testRemoveCardFromHand() {
        player.addCardToHand(redCard);
        assertTrue(player.removeCardFromHand(redCard), "Removing an existing card should return true.");
        assertEquals(0, player.getHandSize(), "Hand should be empty after removing the only card.");
    }

    @Test
    void testRemoveNonExistingCard() {
        assertFalse(player.removeCardFromHand(redCard), "Removing a non-existing card should return false.");
    }

    @Test
    void testCallUnoValid() {
        player.addCardToHand(redCard);
        player.callUno();
        assertTrue(player.hasCalledUno(), "Player should successfully call UNO with one card left.");
    }

    @Test
    void testCallUnoInvalid() {
        player.addCardToHand(redCard);
        player.addCardToHand(blueCard);
        assertThrows(IllegalStateException.class, player::callUno, "Player should not be able to call UNO with more than one card.");
    }

    @Test
    void testIsOnline() {
        assertFalse(player.isOnline(), "Player should be offline by default.");
        player.setOnline(true);
        assertTrue(player.isOnline(), "Player should be online after setting it to true.");
    }

}
