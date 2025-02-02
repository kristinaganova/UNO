package bg.sofia.uni.fmi.mjt.uno.server.card.strategy;

import bg.sofia.uni.fmi.mjt.uno.server.card.strategy.StandardCardEffect;
import bg.sofia.uni.fmi.mjt.uno.server.game.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

class StandardCardEffectTest {
    private Game mockGame;
    private StandardCardEffect cardEffect;

    @BeforeEach
    void setUp() {
        mockGame = mock(Game.class);
        cardEffect = new StandardCardEffect();
    }

    @Test
    void testApplyEffectCallsAdvanceTurn() {
        cardEffect.applyEffect(mockGame);

        verify(mockGame, times(1)).advanceTurn();
    }
}
