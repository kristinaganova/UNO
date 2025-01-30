package bg.sofia.uni.fmi.mjt.uno.card.strategy;

import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.game.components.rules.TurnManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PickColorEffectTest {
    private PickColorEffect pickColorEffect;
    private Game mockGame;
    private TurnManager mockTurnManager;

    @BeforeEach
    void setUp() {
        pickColorEffect = new PickColorEffect();
        mockGame = mock(Game.class);
        mockTurnManager = mock(TurnManager.class);

        when(mockGame.getTurnManager()).thenReturn(mockTurnManager);
    }

    @Test
    void testApplyEffectAdvancesTurn() {
        pickColorEffect.applyEffect(mockGame);
        verify(mockTurnManager, times(1)).advanceTurn();
    }

    @Test
    void testApplyEffectThrowsExceptionForNullGame() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> pickColorEffect.applyEffect(null));

        assertEquals("Game cannot be null", exception.getMessage());
    }
}
