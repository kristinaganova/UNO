package bg.sofia.uni.fmi.mjt.uno.game.components;

import bg.sofia.uni.fmi.mjt.uno.exceptions.game.GameIsFullException;
import bg.sofia.uni.fmi.mjt.uno.exceptions.player.PlayerAlreadyInGameException;
import bg.sofia.uni.fmi.mjt.uno.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerRegistryTest {

    private PlayerRegistry playerRegistry;
    private Player mockPlayer1;
    private Player mockPlayer2;

    @BeforeEach
    void setUp() {
        playerRegistry = new PlayerRegistry(2);
        mockPlayer1 = mock(Player.class);
        mockPlayer2 = mock(Player.class);
    }

    @Test
    void testAddPlayer_Success() {
        playerRegistry.addPlayer(mockPlayer1);
        List<Player> players = playerRegistry.getPlayers();
        assertTrue(players.contains(mockPlayer1), "Player should be added successfully.");
    }

    @Test
    void testAddPlayerAlreadyInGame() {
        playerRegistry.addPlayer(mockPlayer1);
        assertThrows(PlayerAlreadyInGameException.class, () -> playerRegistry.addPlayer(mockPlayer1),
                "Should throw PlayerAlreadyInGameException when adding the same player twice.");
    }

    @Test
    void testAddPlayerGameIsFull() {
        playerRegistry.addPlayer(mockPlayer1);
        playerRegistry.addPlayer(mockPlayer2);
        Player mockPlayer3 = mock(Player.class);
        assertThrows(GameIsFullException.class, () -> playerRegistry.addPlayer(mockPlayer3),
                "Should throw GameIsFullException when adding more players than allowed.");
    }

    @Test
    void testRemovePlayerSuccess() {
        playerRegistry.addPlayer(mockPlayer1);
        playerRegistry.removePlayer(mockPlayer1);
        assertFalse(playerRegistry.getPlayers().contains(mockPlayer1),
                "Player should be removed successfully.");
    }

    @Test
    void testRemovePlayerNotFound() {
        assertThrows(IllegalStateException.class, () -> playerRegistry.removePlayer(mockPlayer1),
                "Should throw IllegalStateException when trying to remove a player not in the game.");
    }

    @Test
    void testMarkPlayerAsFinished() {
        playerRegistry.addPlayer(mockPlayer1);
        playerRegistry.markPlayerAsFinished(mockPlayer1);
        assertFalse(playerRegistry.getPlayers().contains(mockPlayer1),
                "Finished player should be removed from active players.");
        assertTrue(playerRegistry.getFinishedPlayers().contains(mockPlayer1),
                "Finished player should be in finished players list.");
    }

    @Test
    void testSetPlayerAsSpectator() {
        playerRegistry.addPlayer(mockPlayer1);
        playerRegistry.markPlayerAsFinished(mockPlayer1);
        playerRegistry.setPlayerAsSpectator(mockPlayer1);
        assertTrue(playerRegistry.isPlayerSpectator(mockPlayer1),
                "Player should be marked as a spectator.");
    }

    @Test
    void testSetPlayerAsSpectatorNotFinished() {
        playerRegistry.addPlayer(mockPlayer1);
        assertThrows(IllegalStateException.class, () -> playerRegistry.setPlayerAsSpectator(mockPlayer1),
                "Should throw IllegalStateException if player is not finished before becoming a spectator.");
    }

    @Test
    void testHasEnoughPlayers() {
        assertFalse(playerRegistry.hasEnoughPlayers(), "Initially, should not have enough players.");
        playerRegistry.addPlayer(mockPlayer1);
        assertFalse(playerRegistry.hasEnoughPlayers(), "One player is not enough to start the game.");
        playerRegistry.addPlayer(mockPlayer2);
        assertTrue(playerRegistry.hasEnoughPlayers(), "Two players should be enough to start the game.");
    }
}
