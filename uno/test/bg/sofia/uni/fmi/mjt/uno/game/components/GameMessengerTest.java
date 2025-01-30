package bg.sofia.uni.fmi.mjt.uno.game.components;

import bg.sofia.uni.fmi.mjt.uno.player.Player;
import bg.sofia.uni.fmi.mjt.uno.player.account.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class GameMessengerTest {

    private GameMessenger gameMessenger;
    private Player mockPlayer1;
    private Player mockPlayer2;

    @BeforeEach
    void setUp() {
        mockPlayer1 = mock(Player.class);
        mockPlayer2 = mock(Player.class);
        Account mockAccount1 = mock(Account.class);

        when(mockPlayer1.isOnline()).thenReturn(true);
        when(mockPlayer2.isOnline()).thenReturn(false);

        when(mockPlayer1.getAccount()).thenReturn(mockAccount1);
        when(mockAccount1.getUsername()).thenReturn("Player1");

        gameMessenger = new GameMessenger(List.of(mockPlayer1, mockPlayer2));
    }

    @Test
    void testNotifyAll() {
        String message = "Game has started!";

        gameMessenger.notifyAll(message);

        verify(mockPlayer1, times(1)).sendMessage(message);
        verify(mockPlayer2, times(1)).sendMessage(message);
    }

    @Test
    void testNotifyAllThrowsExceptionForNullMessage() {
        assertThrows(IllegalArgumentException.class, () -> gameMessenger.notifyAll(null),
                "Notifying all players with a null message should throw IllegalArgumentException.");
    }

    @Test
    void testNotifyAllThrowsExceptionForEmptyMessage() {
        assertThrows(IllegalArgumentException.class, () -> gameMessenger.notifyAll(""),
                "Notifying all players with an empty message should throw IllegalArgumentException.");
    }

    @Test
    void testNotifyPlayer() {
        String message = "Your turn!";

        gameMessenger.notifyPlayer(mockPlayer1, message);

        verify(mockPlayer1, times(1)).sendMessage("Server: " + message);
    }

    @Test
    void testNotifyPlayerThrowsExceptionForNullPlayer() {
        assertThrows(IllegalArgumentException.class, () -> gameMessenger.notifyPlayer(null, "Message"),
                "Notifying a null player should throw IllegalArgumentException.");
    }

    @Test
    void testNotifyPlayerThrowsExceptionForOfflinePlayer() {
        assertThrows(IllegalArgumentException.class, () -> gameMessenger.notifyPlayer(mockPlayer2, "Message"),
                "Notifying an offline player should throw IllegalArgumentException.");
    }

    @Test
    void testNotifyPlayerThrowsExceptionForNullMessage() {
        assertThrows(IllegalArgumentException.class, () -> gameMessenger.notifyPlayer(mockPlayer1, null),
                "Notifying a player with a null message should throw IllegalArgumentException.");
    }

    @Test
    void testNotifyPlayerThrowsExceptionForEmptyMessage() {
        assertThrows(IllegalArgumentException.class, () -> gameMessenger.notifyPlayer(mockPlayer1, ""),
                "Notifying a player with an empty message should throw IllegalArgumentException.");
    }
}
