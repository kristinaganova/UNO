package bg.sofia.uni.fmi.mjt.uno.server.card.strategy;

import bg.sofia.uni.fmi.mjt.uno.server.card.strategy.SkipTurnEffect;
import bg.sofia.uni.fmi.mjt.uno.server.game.Game;
import bg.sofia.uni.fmi.mjt.uno.server.game.components.rules.TurnManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class SkipTurnEffectTest {
    private SkipTurnEffect skipTurnEffect;
    private Game mockGame;
    private TurnManager mockTurnManager;

    @BeforeEach
    void setUp() {
        skipTurnEffect = new SkipTurnEffect();
        mockGame = mock(Game.class);
        mockTurnManager = mock(TurnManager.class);

        when(mockGame.getTurnManager()).thenReturn(mockTurnManager);
    }

    @Test
    void testApplyEffectCallsSkipTurn() {
        skipTurnEffect.applyEffect(mockGame);

        verify(mockTurnManager, times(1)).skipTurn();
    }

    @Test
    void testApplyEffectThrowsExceptionForNullGame() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> skipTurnEffect.applyEffect(null));

        assertEquals("Game cannot be null", exception.getMessage());
    }
}
