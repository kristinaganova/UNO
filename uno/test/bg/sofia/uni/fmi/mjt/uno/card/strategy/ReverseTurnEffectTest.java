package bg.sofia.uni.fmi.mjt.uno.card.strategy;

import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.game.components.rules.TurnManager;
import bg.sofia.uni.fmi.mjt.uno.game.components.PlayerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ReverseTurnEffectTest {
    private ReverseTurnEffect reverseTurnEffect;
    private Game mockGame;
    private TurnManager mockTurnManager;
    private PlayerRegistry mockPlayerRegistry;

    @BeforeEach
    void setUp() {
        reverseTurnEffect = new ReverseTurnEffect();
        mockGame = mock(Game.class);
        mockTurnManager = mock(TurnManager.class);
        mockPlayerRegistry = mock(PlayerRegistry.class);

        when(mockGame.getTurnManager()).thenReturn(mockTurnManager);
        when(mockGame.getPlayerRegistry()).thenReturn(mockPlayerRegistry);
    }

    @Test
    void testApplyEffectWithMoreThanTwoPlayersReversesDirectionAndAdvancesTurn() {
        when(mockPlayerRegistry.getPlayers()).thenReturn(List.of(mock(), mock(), mock()));

        reverseTurnEffect.applyEffect(mockGame);

        verify(mockTurnManager, times(1)).reverseDirection();
        verify(mockTurnManager, times(1)).advanceTurn();
    }

    @Test
    void testApplyEffectWithTwoPlayersSkipsTurn() {
        when(mockPlayerRegistry.getPlayers()).thenReturn(List.of(mock(), mock()));

        reverseTurnEffect.applyEffect(mockGame);

        verify(mockTurnManager, times(1)).skipTurn();
        verify(mockTurnManager, never()).reverseDirection();
        verify(mockTurnManager, never()).advanceTurn();
    }

    @Test
    void testApplyEffectThrowsExceptionForNullGame() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> reverseTurnEffect.applyEffect(null));

        assertEquals("Game cannot be null", exception.getMessage());
    }
}
