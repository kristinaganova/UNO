package bg.sofia.uni.fmi.mjt.uno.game.components.rules;

import bg.sofia.uni.fmi.mjt.uno.exceptions.game.NoOnlinePlayersException;
import bg.sofia.uni.fmi.mjt.uno.game.components.GameMessenger;
import bg.sofia.uni.fmi.mjt.uno.player.Player;
import bg.sofia.uni.fmi.mjt.uno.player.account.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TurnManagerTest {
    private TurnManager turnManager;
    private Player player1;
    private Player player2;
    private Player player3;
    private GameMessenger gameMessenger;

    @BeforeEach
    void setUp() {
        gameMessenger = mock(GameMessenger.class);

        player1 = new Player(new Account("Player1", "pass"), mock(SocketChannel.class));
        player2 = new Player(new Account("Player2", "pass"), mock(SocketChannel.class));
        player3 = new Player(new Account("Player3", "pass"), mock(SocketChannel.class));

        player1.setOnline(true);
        player2.setOnline(true);
        player3.setOnline(true);

        List<Player> players = List.of(player1, player2, player3);
        turnManager = new TurnManager(players, gameMessenger);
    }

    @Test
    void testGetCurrentPlayerInitially() {
        assertEquals(player1, turnManager.getCurrentPlayer(), "Initial player should be Player1.");
    }

    @Test
    void testAdvanceTurnChangesPlayer() {
        turnManager.advanceTurn();
        assertEquals(player2, turnManager.getCurrentPlayer(), "Turn should advance to Player2.");
    }

    @Test
    void testGetNextPlayer() {
        assertEquals(player2, turnManager.getNextPlayer(), "Next player should be Player2 initially.");
    }

    @Test
    void testSkipTurn() {
        turnManager.skipTurn();
        assertEquals(player3, turnManager.getCurrentPlayer(), "Skipping should move two places forward.");
    }

    @Test
    void testReverseDirection() {
        turnManager.reverseDirection();
        turnManager.advanceTurn();
        assertEquals(player3, turnManager.getCurrentPlayer(), "After reversing, turn should go to previous player.");
    }

    @Test
    void testAdvanceTurnWithOfflinePlayers() {
        player2.setOnline(false);
        turnManager.advanceTurn();
        assertEquals(player3, turnManager.getCurrentPlayer(), "Turn should skip offline player2.");
    }

    @Test
    void testAdvanceTurnThrowsNoOnlinePlayersException() {
        player1.setOnline(false);
        player2.setOnline(false);
        player3.setOnline(false);

        assertThrows(NoOnlinePlayersException.class, turnManager::advanceTurn,
                "Should throw exception if no players are online.");
    }

    @Test
    void testAnnounceTurnCallsGameMessenger() {
        turnManager.announceTurn();
        verify(gameMessenger).notifyAll(contains("Player1"));
    }
}
