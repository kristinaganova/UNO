package bg.sofia.uni.fmi.mjt.uno.server.card.strategy;

import bg.sofia.uni.fmi.mjt.uno.server.card.strategy.PlusTwoEffect;
import bg.sofia.uni.fmi.mjt.uno.server.game.Game;
import bg.sofia.uni.fmi.mjt.uno.server.game.components.DeckHandler;
import bg.sofia.uni.fmi.mjt.uno.server.game.components.rules.TurnManager;
import bg.sofia.uni.fmi.mjt.uno.server.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PlusTwoEffectTest {
    private PlusTwoEffect plusTwoEffect;
    private Game mockGame;
    private TurnManager mockTurnManager;
    private DeckHandler mockDeckHandler;
    private Player mockNextPlayer;

    @BeforeEach
    void setUp() {
        plusTwoEffect = new PlusTwoEffect();
        mockGame = mock(Game.class);
        mockTurnManager = mock(TurnManager.class);
        mockDeckHandler = mock(DeckHandler.class);
        mockNextPlayer = mock(Player.class);

        when(mockGame.getTurnManager()).thenReturn(mockTurnManager);
        when(mockGame.getDeckHandler()).thenReturn(mockDeckHandler);
        when(mockTurnManager.getNextPlayer()).thenReturn(mockNextPlayer);
    }

    @Test
    void testApplyEffectDrawsTwoCardsAndSkipsTurn() {
        plusTwoEffect.applyEffect(mockGame);

        verify(mockDeckHandler, times(2)).drawCard(mockNextPlayer);
        verify(mockTurnManager, times(1)).skipTurn();
    }

    @Test
    void testApplyEffectThrowsExceptionForNullGame() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> plusTwoEffect.applyEffect(null));

        assertEquals("Game cannot be null", exception.getMessage());
    }
}
