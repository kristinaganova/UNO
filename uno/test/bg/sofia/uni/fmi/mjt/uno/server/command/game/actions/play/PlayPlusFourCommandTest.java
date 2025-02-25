package bg.sofia.uni.fmi.mjt.uno.server.command.game.actions.play;

import bg.sofia.uni.fmi.mjt.uno.server.card.models.WildCard;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.WildCardType;
import bg.sofia.uni.fmi.mjt.uno.server.deck.UnoDeck;
import bg.sofia.uni.fmi.mjt.uno.server.exceptions.command.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.Game;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.components.DeckHandler;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.components.GameMessenger;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.components.rules.TurnManager;
import bg.sofia.uni.fmi.mjt.uno.server.loggers.CardLogger;
import bg.sofia.uni.fmi.mjt.uno.server.player.Hand;
import bg.sofia.uni.fmi.mjt.uno.server.player.Player;
import bg.sofia.uni.fmi.mjt.uno.server.player.account.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayPlusFourCommandTest {

    private Player mockPlayer;
    private Game mockGame;
    private DeckHandler mockDeckHandler;
    private UnoDeck mockDeck;
    private WildCard mockWildCard;
    private Hand mockHand;
    private PlayChooseColorCommand command;
    private CardLogger logger;
    private Account account;
    private GameMessenger messenger;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        mockGame = mock(Game.class);
        mockDeckHandler = mock(DeckHandler.class);
        mockDeck = mock(UnoDeck.class);
        logger = mock(CardLogger.class);
        account = mock(Account.class);
        messenger = mock(GameMessenger.class);

        when(mockGame.getDeckHandler()).thenReturn(mockDeckHandler);
        when(mockDeckHandler.getDeck()).thenReturn(mockDeck);
        when(mockGame.getLogger()).thenReturn(logger);
        when(mockGame.getGameMessenger()).thenReturn(messenger);

        mockWildCard = mock(WildCard.class);
        when(mockWildCard.getId()).thenReturn("123");
        when(mockWildCard.getWildCardType()).thenReturn(WildCardType.PICK_COLOR);

        mockHand = mock(Hand.class);
        when(mockPlayer.getHandManager()).thenReturn(mockHand);
        when(mockHand.getAllCards()).thenReturn(List.of(mockWildCard));

        TurnManager mockTurnManager = mock(TurnManager.class);
        when(mockGame.getTurnManager()).thenReturn(mockTurnManager);
        when(mockTurnManager.getCurrentPlayer()).thenReturn(mockPlayer);

        when(mockPlayer.getAccount()).thenReturn(account);
        when(account.username()).thenReturn("test");
        command = new PlayChooseColorCommand(mockPlayer, mockGame);
    }

    @Test
    void testExecutePlayerCommandValidChooseColor() {
        String[] args = {"--card-id=123", "--color=blue"};

        String result = command.executePlayerCommand(args);

        assertEquals("You played a choose color WildCard. The color is now BLUE.", result,
                "Expected the command to execute successfully and return a success message.");
    }

    @Test
    void testExecutePlayerCommandWithMissingArgumentsThrowsException() {
        String[] args = {"--card-id=123"};

        assertThrows(IllegalArgumentException.class,
                () -> command.executePlayerCommand(args),
                "Expected an exception due to missing color argument.");
    }

    @Test
    void testExecutePlayerCommandWithInvalidColorThrowsException() {
        String[] args = {"--card-id=123", "--color=invalidColor"};

        assertThrows(CommandExecutionException.class,
                () -> command.executePlayerCommand(args),
                "Expected an exception due to invalid color argument.");
    }
}
