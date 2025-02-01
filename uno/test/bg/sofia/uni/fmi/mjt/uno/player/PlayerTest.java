package bg.sofia.uni.fmi.mjt.uno.player;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.card.models.StandardCard;
import bg.sofia.uni.fmi.mjt.uno.card.strategy.StandardCardEffect;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.game.components.DeckHandler;
import bg.sofia.uni.fmi.mjt.uno.player.account.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.ByteBuffer;
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

        when(mockAccount.getUsername()).thenReturn("TestPlayer");
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

    @Test
    void testSetGameSuccess() {
        player.setGame(mockGame);
        assertEquals(mockGame, player.getCurrentGame(), "Player should be assigned to the correct game.");
    }

    @Test
    void testSetGameThrowsExceptionForNull() {
        assertThrows(IllegalArgumentException.class, () -> player.setGame(null),
                "Setting a null game should throw IllegalArgumentException.");
    }

    @Test
    void testSendMessageSuccess() throws IOException {
        when(mockClient.isOpen()).thenReturn(true);
        player.sendMessage("Hello!");
        verify(mockClient, times(1)).write(any(ByteBuffer.class));
    }

    @Test
    void testSendMessageHandlesIOException() throws IOException {
        when(mockClient.isOpen()).thenReturn(true);
        doThrow(new IOException("Connection lost")).when(mockClient).write(any(ByteBuffer.class));
        player.sendMessage("Hello!");
    }

    @Test
    void testEqualsAndHashCode() {
        Player player1 = new Player(mockAccount, mockClient);
        Player player2 = new Player(mockAccount, mockClient);

        assertEquals(player1, player2, "Players with the same account should be equal.");
        assertEquals(player1.hashCode(), player2.hashCode(), "Hash codes should match for equal players.");
    }

    @Test
    void testShowHand() {
        when(mockGame.getDeckHandler()).thenReturn(mock(DeckHandler.class));
        when(mockGame.getDeckHandler().getTopDiscardCard()).thenReturn(redCard);
        when(mockGame.getDeckHandler().getCurrentColor()).thenReturn(Color.RED);
        player.setGame(mockGame);

        player.addCardToHand(redCard);
        String handDescription = player.showHand();

        assertTrue(handDescription.contains("Your hand:"), "Hand description should contain 'Your hand'.");
        assertTrue(handDescription.contains("RED"), "Hand description should contain color of the card.");
        assertTrue(handDescription.contains("1"), "Hand description should contain the card number.");
    }
}
