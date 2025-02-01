package bg.sofia.uni.fmi.mjt.uno.game;

import bg.sofia.uni.fmi.mjt.uno.exceptions.game.NotEnoughPlayersException;
import bg.sofia.uni.fmi.mjt.uno.exceptions.player.PlayerNotPermittedException;

import bg.sofia.uni.fmi.mjt.uno.player.Hand;
import bg.sofia.uni.fmi.mjt.uno.player.Player;
import bg.sofia.uni.fmi.mjt.uno.player.account.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameTest {

    private Game game;
    private Player mockCreator;
    private Player mockPlayer2;
    private Account mockAccount1;
    private Account mockAccount2;

    @BeforeEach
    void setUp() {
        mockAccount1 = mock(Account.class);
        mockAccount2 = mock(Account.class);
        when(mockAccount1.getUsername()).thenReturn("Player1");
        when(mockAccount2.getUsername()).thenReturn("Player2");

        mockCreator = mock(Player.class);
        when(mockCreator.getAccount()).thenReturn(mockAccount1);
        when(mockCreator.isOnline()).thenReturn(true);

        mockPlayer2 = mock(Player.class);
        when(mockPlayer2.getAccount()).thenReturn(mockAccount2);
        when(mockPlayer2.isOnline()).thenReturn(true);

        game = new Game("game1", 2, mockCreator);

        Hand mockHand = mock(Hand.class);
        when(mockCreator.getHand()).thenReturn(mockHand);
        when(mockPlayer2.getHand()).thenReturn(mockHand);
    }

    @Test
    void testGameCreation_Success() {
        assertNotNull(game.getId(), "Game ID should not be null.");
        assertEquals("game1", game.getId(), "Game ID should match the expected value.");
        assertEquals(mockCreator, game.getCreator(), "Game creator should be correctly assigned.");
    }

    @Test
    void testGameCreation_InvalidParameters() {
        assertThrows(IllegalArgumentException.class, () -> new Game("", 2, mockCreator),
                "Game ID cannot be empty.");
        assertThrows(IllegalArgumentException.class, () -> new Game("game2", 1, mockCreator),
                "Should not allow games with less than 2 players.");
        assertThrows(IllegalArgumentException.class, () -> new Game("game3", 11, mockCreator),
                "Should not allow games with more than 10 players.");
        assertThrows(IllegalArgumentException.class, () -> new Game("game4", 2, null),
                "Game creator cannot be null.");
    }

    @Test
    void testStartGame_Success() {
        game.getPlayerRegistry().addPlayer(mockCreator);
        game.getPlayerRegistry().addPlayer(mockPlayer2);

        game.startGame(mockCreator);

        assertNotNull(game.getTurnManager(), "Turn manager should be initialized.");
        assertEquals(GameState.STARTED, game.getGameState(), "Game state should be STARTED.");
    }

    @Test
    void testStartGameNotEnoughPlayers() {
        game.getPlayerRegistry().addPlayer(mockCreator);

        assertThrows(NotEnoughPlayersException.class, () -> game.startGame(mockCreator),
                "Should not start a game without at least 2 players.");
    }

    @Test
    void testStartGameNotCreator() {
        game.getPlayerRegistry().addPlayer(mockCreator);
        game.getPlayerRegistry().addPlayer(mockPlayer2);

        assertThrows(PlayerNotPermittedException.class, () -> game.startGame(mockPlayer2),
                "Only the creator should be able to start the game.");
    }

    @Test
    void testDisconnectPlayer() {
        game.getPlayerRegistry().addPlayer(mockCreator);
        game.getPlayerRegistry().addPlayer(mockPlayer2);

        game.disconnectPlayer("Player2");

        verify(mockPlayer2, times(1)).setOnline(false);
    }

    @Test
    void testReconnectPlayerSuccess() {
        game.getPlayerRegistry().addPlayer(mockCreator);
        game.getPlayerRegistry().addPlayer(mockPlayer2);
        game.disconnectPlayer("Player2");

        assertTrue(game.reconnectPlayer("Player2"), "Player should be able to reconnect.");
    }

    @Test
    void testReconnectPlayerFailure() {
        assertFalse(game.reconnectPlayer("Unknown"), "Unknown player should not be able to reconnect.");
    }

    @Test
    void testEndGame() {
        game.getPlayerRegistry().addPlayer(mockCreator);
        game.getPlayerRegistry().addPlayer(mockPlayer2);

        game.startGame(mockCreator);
        game.endGame();

        assertEquals(GameState.FINISHED, game.getGameState(), "Game should be in FINISHED state.");
    }

    @Test
    void testLeaveGameLastPlayerEndsGame() {
        game.getPlayerRegistry().addPlayer(mockCreator);

        game.leaveGame(mockCreator);

        assertEquals(GameState.FINISHED, game.getGameState(),
                "Game should end when the last player leaves.");
    }

    @Test
    void testGetSummary() {
        game.getPlayerRegistry().addPlayer(mockCreator);
        game.getPlayerRegistry().addPlayer(mockPlayer2);

        String summary = game.getSummary();
        assertTrue(summary.contains("Game ID: game1"), "Summary should contain the game ID.");
        assertTrue(summary.contains("Creator: Player1"), "Summary should contain the creator.");
        assertTrue(summary.contains("Player2"), "Summary should list Player2.");
    }
}
