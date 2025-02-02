package bg.sofia.uni.fmi.mjt.uno.server.command.game.actions.play;

import bg.sofia.uni.fmi.mjt.uno.server.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.server.card.models.StandardCard;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.server.deck.Deck;
import bg.sofia.uni.fmi.mjt.uno.server.deck.UnoDeck;
import bg.sofia.uni.fmi.mjt.uno.server.game.Game;
import bg.sofia.uni.fmi.mjt.uno.server.game.components.DeckHandler;
import bg.sofia.uni.fmi.mjt.uno.server.game.components.rules.TurnManager;
import bg.sofia.uni.fmi.mjt.uno.server.player.Hand;
import bg.sofia.uni.fmi.mjt.uno.server.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayCardCommandTest {
    private Player mockPlayer;
    private Game mockGame;
    private PlayCardCommand playCardCommand;
    private DeckHandler mockDeckHandler;
    private Card mockCard;
    private Card mockTopCard;
    private Hand mockHand;
    private UnoDeck mockDeck;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        mockGame = mock(Game.class);
        mockDeckHandler = mock(DeckHandler.class);
        mockDeck = mock(UnoDeck.class);
        when(mockGame.getDeckHandler()).thenReturn(mockDeckHandler);

        mockTopCard = mock(StandardCard.class);
        when(mockDeckHandler.getTopDiscardCard()).thenReturn(mockTopCard);
        when(mockDeckHandler.getCurrentColor()).thenReturn(Color.RED);
        when(mockDeckHandler.getDeck()).thenReturn(mockDeck);
        playCardCommand = new PlayCardCommand(mockPlayer, mockGame);

        mockCard = mock(StandardCard.class);
        when(mockCard.getId()).thenReturn("123");
        mockHand = mock(Hand.class);

        TurnManager mockTurnManager = mock(TurnManager.class);
        when(mockGame.getTurnManager()).thenReturn(mockTurnManager);
        when(mockTurnManager.getCurrentPlayer()).thenReturn(mockPlayer);

        when(mockPlayer.getHandManager()).thenReturn(mockHand);
        when(mockHand.getAllCards()).thenReturn(List.of(mockCard));

        when(mockCard.getColor()).thenReturn(Color.BLACK);
        when(mockTopCard.getColor()).thenReturn(Color.RED);
        when(mockCard.isPlayable(any(), any())).thenReturn(true);
    }


    @Test
    void testExecutePlayerCommandValidCardButIncorrectCommand() {
        when(mockGame.getDeckHandler().getDeck().getTopDiscardCard()).thenReturn(mockTopCard);
        when(mockGame.getDeckHandler().getCurrentColor()).thenReturn(Color.RED);

        String[] args = {"--card-id=123"};

        assertThrows(IllegalArgumentException.class, () -> playCardCommand.executePlayerCommand(args));
    }

    @Test
    void testExecutePlayerCommandWithBlackCardThrowsException() {
        when(mockCard.getColor()).thenReturn(Color.BLACK);

        String[] args = {"--card-id=123"};

        assertThrows(IllegalArgumentException.class,
                () -> playCardCommand.executePlayerCommand(args),
                "Playing a black card should throw an exception.");
    }

    @Test
    void testExecutePlayerCommandWithInvalidArgumentsThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> playCardCommand.executePlayerCommand(null),
                "Null arguments should throw an exception.");

        assertThrows(IllegalArgumentException.class,
                () -> playCardCommand.executePlayerCommand(new String[]{}),
                "Empty arguments should throw an exception.");
    }

    @Test
    void testExecutePlayerCommandWithNonPlayableCardThrowsException() {
        when(mockCard.isPlayable(any(), any())).thenReturn(false);

        String[] args = {"--card-id=123"};

        assertThrows(IllegalArgumentException.class,
                () -> playCardCommand.executePlayerCommand(args),
                "Playing a non-playable card should throw an exception.");
    }
}
