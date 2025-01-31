package bg.sofia.uni.fmi.mjt.uno.games;

import bg.sofia.uni.fmi.mjt.uno.exceptions.game.GameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.uno.exceptions.game.GameNotAvailableException;
import bg.sofia.uni.fmi.mjt.uno.exceptions.game.GameNotFoundException;
import bg.sofia.uni.fmi.mjt.uno.exceptions.player.PlayerNotPermittedException;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;
import bg.sofia.uni.fmi.mjt.uno.player.account.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameManagerTest {

    private GameManager gameManager;
    private Player mockPlayer1;
    private Player mockPlayer2;
    private Account mockAccount1;
    private Account mockAccount2;

    @BeforeEach
    void setUp() {;
        gameManager = GameManager.getInstance();
        gameManager.clearGames();

        mockAccount1 = mock(Account.class);
        mockAccount2 = mock(Account.class);
        mockPlayer1 = mock(Player.class);
        mockPlayer2 = mock(Player.class);

        when(mockAccount1.getUsername()).thenReturn("player1");
        when(mockAccount2.getUsername()).thenReturn("player2");
        when(mockPlayer1.getAccount()).thenReturn(mockAccount1);
        when(mockPlayer2.getAccount()).thenReturn(mockAccount2);

        when(mockPlayer1.isOnline()).thenReturn(true);
        when(mockPlayer2.isOnline()).thenReturn(true);
    }

    @Test
    void testCreateGameSuccess() {
        assertDoesNotThrow(() -> gameManager.createGame("game1", 3, mockPlayer1));
        assertTrue(gameManager.doesGameExist("game1"));
    }

    @Test
    void testCreateGameFailDuplicateGameId() {
        gameManager.createGame("game1", 3, mockPlayer1);
        assertThrows(GameAlreadyExistsException.class, () -> gameManager.createGame("game1", 3, mockPlayer1));
    }

    @Test
    void testCreateGameFailInvalidPlayersCount() {
        assertThrows(IllegalArgumentException.class, () -> gameManager.createGame("game1", 1, mockPlayer1));
        assertThrows(IllegalArgumentException.class, () -> gameManager.createGame("game1", 11, mockPlayer1));
    }

    @Test
    void testJoinGameSuccess() {
        gameManager.createGame("game1", 3, mockPlayer1);
        assertDoesNotThrow(() -> gameManager.joinGame("game1", mockPlayer2));
    }

    @Test
    void testJoinGameFailInvalidGameId() {
        assertThrows(GameNotAvailableException.class, () -> gameManager.joinGame("nonexistent", mockPlayer1));
    }

    @Test
    void testStartGameSuccess() {
        gameManager.createGame("game1", 3, mockPlayer1);
        gameManager.joinGame("game1", mockPlayer2);
        gameManager.joinGame("game1", mockPlayer1);
        assertDoesNotThrow(() -> gameManager.startGame("game1", mockPlayer1));
    }

    @Test
    void testStartGameNotExists() {
        assertThrows(GameNotFoundException.class, () -> gameManager.startGame("game1", mockPlayer1));
    }

    @Test
    void testStartGameNullGameID() {
        assertThrows(IllegalArgumentException.class, () -> gameManager.startGame(null, mockPlayer1));
    }

    @Test
    void testStartGameNullCreator() {
        gameManager.createGame("game1", 3, mockPlayer1);
        gameManager.joinGame("game1", mockPlayer2);
        gameManager.joinGame("game1", mockPlayer1);
        assertThrows(IllegalArgumentException.class, () -> gameManager.startGame("game1", null));
    }

    @Test
    void testStartGameFailNonCreatorStarts() {
        gameManager.createGame("game1", 3, mockPlayer1);
        assertThrows(PlayerNotPermittedException.class, () -> gameManager.startGame("game1", mockPlayer2));
    }

    @Test
    void testRemoveGameSuccess() {
        gameManager.createGame("game1", 3, mockPlayer1);
        gameManager.removeGame("game1");
        assertFalse(gameManager.doesGameExist("game1"));
    }

    @Test
    void testRemoveGameFailNonexistentGame() {
        assertDoesNotThrow(() -> gameManager.removeGame("nonexistent"));
    }

    @Test
    void testGetGamesByStatus() {
        gameManager.createGame("game1", 3, mockPlayer1);
        gameManager.createGame("game2", 3, mockPlayer1);

        String availableGames = gameManager.getGamesByStatus("available");

        assertTrue(availableGames.contains("game2"));
    }

    @Test
    void testGetGameByPlayer() {
        gameManager.createGame("game1", 3, mockPlayer1);
        gameManager.joinGame("game1", mockPlayer2);

        Game game = gameManager.getGameByPlayer("player2");
        assertNotNull(game);
        assertEquals("game1", game.getId());
    }

    @Test
    void testIsPlayerInAnyGame() {
        gameManager.createGame("game1", 3, mockPlayer1);
        gameManager.joinGame("game1", mockPlayer2);

        assertTrue(gameManager.isPlayerInAnyGame("player2"));
        assertFalse(gameManager.isPlayerInAnyGame("unknown"));
    }

    @Test
    void testNotifyPlayersNullGame() {
        assertThrows(IllegalArgumentException.class, () -> gameManager.notifyPlayersInGame(null, "message"));
    }

    @Test
    void testNotifyPlayersNullMessage() {
        gameManager.createGame("game1", 3, mockPlayer1);
        Game game = gameManager.getGame("game1");
        assertThrows(IllegalArgumentException.class, () -> gameManager.notifyPlayersInGame(game, null));
    }

    @Test
    void removeGameNullGame() {
        assertThrows(IllegalArgumentException.class, () -> gameManager.removeGame(null));
    }

    @Test
    void getGamesByStatusNull() {
        assertThrows(IllegalArgumentException.class, () -> gameManager.getGamesByStatus(null));
    }
}
