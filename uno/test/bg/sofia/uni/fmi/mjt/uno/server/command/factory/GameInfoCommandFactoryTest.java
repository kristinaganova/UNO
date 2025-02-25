package bg.sofia.uni.fmi.mjt.uno.server.command.factory;

import bg.sofia.uni.fmi.mjt.uno.server.command.Command;
import bg.sofia.uni.fmi.mjt.uno.server.command.factory.GameInfoCommandFactory;
import bg.sofia.uni.fmi.mjt.uno.server.command.game.info.ShowHandCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.game.info.ShowLastCardCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.game.info.ShowPlayedCardsCommand;
import bg.sofia.uni.fmi.mjt.uno.server.exceptions.command.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.Game;
import bg.sofia.uni.fmi.mjt.uno.server.player.Player;
import bg.sofia.uni.fmi.mjt.uno.server.player.account.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameInfoCommandFactoryTest {

    private UserManager mockUserManager;
    private Player mockPlayer;
    private Game mockGame;
    private SocketChannel mockClient;
    private GameInfoCommandFactory gameInfoCommandFactory;

    @BeforeEach
    void setUp() {
        mockUserManager = mock(UserManager.class);
        mockPlayer = mock(Player.class);
        mockGame = mock(Game.class);
        mockClient = mock(SocketChannel.class);

        gameInfoCommandFactory = new GameInfoCommandFactory(mockUserManager);

        when(mockUserManager.getLoggedInUsername(mockClient)).thenReturn("player1");
        when(mockUserManager.getPlayerByUsername("player1")).thenReturn(mockPlayer);
        when(mockPlayer.getCurrentGame()).thenReturn(mockGame);
    }

    @Test
    void testSupportsValidCommands() {
        assertTrue(gameInfoCommandFactory.supports("show-hand"));
        assertTrue(gameInfoCommandFactory.supports("show-last-card"));
        assertTrue(gameInfoCommandFactory.supports("show-played-cards"));
    }

    @Test
    void testSupportsInvalidCommand() {
        assertFalse(gameInfoCommandFactory.supports("invalid-command"));
    }

    @Test
    void testCreateCommandShowHand() {
        Command command = gameInfoCommandFactory.createCommand("show-hand", mockClient);
        assertInstanceOf(ShowHandCommand.class, command);
    }

    @Test
    void testCreateCommandShowLastCard() {
        Command command = gameInfoCommandFactory.createCommand("show-last-card", mockClient);
        assertInstanceOf(ShowLastCardCommand.class, command);
    }

    @Test
    void testCreateCommandShowPlayedCards() {
        Command command = gameInfoCommandFactory.createCommand("show-played-cards", mockClient);
        assertInstanceOf(ShowPlayedCardsCommand.class, command);
    }

    @Test
    void testCreateCommandThrowsExceptionForUnknownCommand() {
        Exception exception = assertThrows(CommandNotFoundException.class,
                () -> gameInfoCommandFactory.createCommand("invalidCommand", mockClient));
        assertEquals("invalidCommand", exception.getMessage());
    }

    @Test
    void testCreateCommandThrowsExceptionIfPlayerNotLoggedIn() {
        when(mockUserManager.getLoggedInUsername(mockClient)).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> gameInfoCommandFactory.createCommand("show-hand", mockClient));
        assertEquals("Client is not logged in.", exception.getMessage());
    }

    @Test
    void testCreateCommandThrowsExceptionIfPlayerNotFound() {
        when(mockUserManager.getPlayerByUsername("player1")).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> gameInfoCommandFactory.createCommand("show-hand", mockClient));
        assertEquals("No player found for the logged-in user.", exception.getMessage());
    }

    @Test
    void testCreateCommandThrowsExceptionIfPlayerNotInGame() {
        when(mockPlayer.getCurrentGame()).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> gameInfoCommandFactory.createCommand("show-hand", mockClient));
        assertEquals("Player is not part of any game.", exception.getMessage());
    }
}
