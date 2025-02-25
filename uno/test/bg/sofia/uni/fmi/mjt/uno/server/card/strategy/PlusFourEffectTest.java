package bg.sofia.uni.fmi.mjt.uno.server.card.strategy;

import bg.sofia.uni.fmi.mjt.uno.server.card.strategy.PlusFourEffect;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.Game;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.components.DeckHandler;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.components.rules.TurnManager;
import bg.sofia.uni.fmi.mjt.uno.server.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlusFourEffectTest {
    private PlusFourEffect plusFourEffect;
    private Game mockGame;
    private TurnManager mockTurnManager;
    private DeckHandler mockDeckHandler;
    private Player mockNextPlayer;

    @BeforeEach
    void setUp() {
        plusFourEffect = new PlusFourEffect();
        mockGame = mock(Game.class);
        mockTurnManager = mock(TurnManager.class);
        mockDeckHandler = mock(DeckHandler.class);
        mockNextPlayer = mock(Player.class);

        when(mockGame.getTurnManager()).thenReturn(mockTurnManager);
        when(mockGame.getDeckHandler()).thenReturn(mockDeckHandler);
        when(mockTurnManager.getNextPlayer()).thenReturn(mockNextPlayer);
    }

    @Test
    void testApplyEffectDrawsFourCardsAndSkipsTurn() {
        plusFourEffect.applyEffect(mockGame);

        verify(mockDeckHandler, times(4)).drawCard(mockNextPlayer);
        verify(mockTurnManager, times(1)).skipTurn();
    }

    @Test
    void testApplyEffectThrowsExceptionForNullGame() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> plusFourEffect.applyEffect(null));

        assertEquals("Game cannot be null", exception.getMessage());
    }
}
