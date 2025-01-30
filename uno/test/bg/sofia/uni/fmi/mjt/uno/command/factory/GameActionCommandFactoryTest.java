package bg.sofia.uni.fmi.mjt.uno.command.factory;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.command.game.actions.*;
import bg.sofia.uni.fmi.mjt.uno.command.game.actions.play.*;
import bg.sofia.uni.fmi.mjt.uno.command.game.info.SpectateCommand;
import bg.sofia.uni.fmi.mjt.uno.exceptions.command.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;
import bg.sofia.uni.fmi.mjt.uno.player.account.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameActionCommandFactoryTest {

    private UserManager mockUserManager;
    private Player mockPlayer;
    private Game mockGame;
    private SocketChannel mockClient;
    private GameActionCommandFactory gameActionCommandFactory;

    @BeforeEach
    void setUp() {
        mockUserManager = mock(UserManager.class);
        mockPlayer = mock(Player.class);
        mockGame = mock(Game.class);
        mockClient = mock(SocketChannel.class);

        gameActionCommandFactory = new GameActionCommandFactory(mockUserManager);

        when(mockUserManager.getLoggedInUsername(mockClient)).thenReturn("player1");
        when(mockUserManager.getPlayerByUsername("player1")).thenReturn(mockPlayer);
        when(mockPlayer.getCurrentGame()).thenReturn(mockGame);
    }

    @Test
    void testSupportsValidCommands() {
        assertTrue(gameActionCommandFactory.supports("play-card"));
        assertTrue(gameActionCommandFactory.supports("play-choose-color"));
        assertTrue(gameActionCommandFactory.supports("play-plus-four"));
        assertTrue(gameActionCommandFactory.supports("draw-card"));
        assertTrue(gameActionCommandFactory.supports("leave"));
        assertTrue(gameActionCommandFactory.supports("spectate"));
        assertTrue(gameActionCommandFactory.supports("uno"));
        assertTrue(gameActionCommandFactory.supports("stop-uno"));
    }

    @Test
    void testSupportsInvalidCommand() {
        assertFalse(gameActionCommandFactory.supports("invalid-command"));
    }

    @Test
    void testCreateCommandPlayCard() {
        Command command = gameActionCommandFactory.createCommand("play-card", mockClient);
        assertInstanceOf(PlayCardCommand.class, command);
    }

    @Test
    void testCreateCommandPlayChooseColor() {
        Command command = gameActionCommandFactory.createCommand("play-choose-color", mockClient);
        assertInstanceOf(PlayChooseColorCommand.class, command);
    }

    @Test
    void testCreateCommandPlayPlusFour() {
        Command command = gameActionCommandFactory.createCommand("play-plus-four", mockClient);
        assertInstanceOf(PlayPlusFourCommand.class, command);
    }

    @Test
    void testCreateCommandDrawCard() {
        Command command = gameActionCommandFactory.createCommand("draw-card", mockClient);
        assertInstanceOf(DrawCardCommand.class, command);
    }

    @Test
    void testCreateCommandLeave() {
        Command command = gameActionCommandFactory.createCommand("leave", mockClient);
        assertInstanceOf(LeaveCommand.class, command);
    }

    @Test
    void testCreateCommandSpectate() {
        Command command = gameActionCommandFactory.createCommand("spectate", mockClient);
        assertInstanceOf(SpectateCommand.class, command);
    }

    @Test
    void testCreateCommandUno() {
        Command command = gameActionCommandFactory.createCommand("uno", mockClient);
        assertInstanceOf(UnoCommand.class, command);
    }

    @Test
    void testCreateCommandStopUno() {
        Command command = gameActionCommandFactory.createCommand("stop-uno", mockClient);
        assertInstanceOf(StopUnoCommand.class, command);
    }

    @Test
    void testCreateCommandThrowsExceptionForUnknownCommand() {
        Exception exception = assertThrows(CommandNotFoundException.class,
                () -> gameActionCommandFactory.createCommand("invalidCommand", mockClient));
        assertEquals("Unknown command: invalidCommand", exception.getMessage());
    }

    @Test
    void testCreateCommandThrowsExceptionIfPlayerNotLoggedIn() {
        when(mockUserManager.getLoggedInUsername(mockClient)).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> gameActionCommandFactory.createCommand("play-card", mockClient));
        assertEquals("Client is not logged in.", exception.getMessage());
    }

    @Test
    void testCreateCommandThrowsExceptionIfPlayerNotFound() {
        when(mockUserManager.getPlayerByUsername("player1")).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> gameActionCommandFactory.createCommand("play-card", mockClient));
        assertEquals("No player found for the logged-in user.", exception.getMessage());
    }

    @Test
    void testCreateCommandThrowsExceptionIfPlayerNotInGame() {
        when(mockPlayer.getCurrentGame()).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> gameActionCommandFactory.createCommand("play-card", mockClient));
        assertEquals("Player is not part of any game.", exception.getMessage());
    }
}
