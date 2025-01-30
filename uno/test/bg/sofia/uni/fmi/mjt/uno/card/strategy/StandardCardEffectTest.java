package bg.sofia.uni.fmi.mjt.uno.card.strategy;

import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.game.components.rules.TurnManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class StandardCardEffectTest {
    private StandardCardEffect standardCardEffect;
    private Game mockGame;
    private TurnManager mockTurnManager;

    @BeforeEach
    void setUp() {
        standardCardEffect = new StandardCardEffect();
        mockGame = mock(Game.class);
        mockTurnManager = mock(TurnManager.class);

        when(mockGame.getTurnManager()).thenReturn(mockTurnManager);
    }

    @Test
    void testApplyEffectCallsAdvanceTurn() {
        standardCardEffect.applyEffect(mockGame);

        verify(mockTurnManager, times(1)).advanceTurn();
    }

    @Test
    void testApplyEffectThrowsExceptionForNullGame() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> standardCardEffect.applyEffect(null));

        assertEquals("Game cannot be null", exception.getMessage());
    }
}
