package bg.sofia.uni.fmi.mjt.uno.server.games.game.components.rules;

import bg.sofia.uni.fmi.mjt.uno.server.games.game.components.PlayerRegistry;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.components.rules.GameRules;
import bg.sofia.uni.fmi.mjt.uno.server.player.Player;
import bg.sofia.uni.fmi.mjt.uno.server.player.account.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameRulesTest {
    private GameRules gameRules;
    private PlayerRegistry playerRegistry;
    private Player player1;
    private Player player2;
    private Player player3;

    @BeforeEach
    void setUp() {
        playerRegistry = mock(PlayerRegistry.class);

        player1 = new Player(new Account("Player1", "pass"), mock(SocketChannel.class));
        player2 = new Player(new Account("Player2", "pass"), mock(SocketChannel.class));
        player3 = new Player(new Account("Player3", "pass"), mock(SocketChannel.class));

        gameRules = new GameRules(playerRegistry);
    }

    @Test
    void testIsGameOverWithSinglePlayer() {
        when(playerRegistry.getPlayers()).thenReturn(List.of(player1));

        assertTrue(gameRules.isGameOver(), "Game should be over when there is only one player left.");
    }

    @Test
    void testIsGameOverWithMultiplePlayers() {
        when(playerRegistry.getPlayers()).thenReturn(List.of(player1, player2));

        assertFalse(gameRules.isGameOver(), "Game should not be over when multiple players remain.");
    }

}
