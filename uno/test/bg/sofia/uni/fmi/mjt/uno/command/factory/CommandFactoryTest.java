package bg.sofia.uni.fmi.mjt.uno.command.factory;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.exceptions.command.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.player.Player;
import bg.sofia.uni.fmi.mjt.uno.player.account.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandFactoryTest {

    private UserManager mockUserManager;
    private GameManager mockGameManager;
    private SocketChannel mockClient;
    private CommandFactory commandFactory;

    // Mock the sub-factories
    private AuthCommandFactory mockAuthFactory;
    private GameManagementCommandFactory mockGameManagementFactory;
    private GameActionCommandFactory mockGameActionFactory;
    private GameInfoCommandFactory mockGameInfoFactory;

    @BeforeEach
    void setUp() {
        mockUserManager = mock(UserManager.class);
        mockGameManager = mock(GameManager.class);
        mockClient = mock(SocketChannel.class);

        mockAuthFactory = mock(AuthCommandFactory.class);
        mockGameManagementFactory = mock(GameManagementCommandFactory.class);
        mockGameActionFactory = mock(GameActionCommandFactory.class);
        mockGameInfoFactory = mock(GameInfoCommandFactory.class);

        Player mockPlayer = mock(Player.class);
        when(mockPlayer.getCurrentGame()).thenReturn(mock(Game.class));

        commandFactory = new CommandFactory(mockUserManager, mockGameManager);
    }

    @Test
    void testCreateCommandDelegatesToAuthFactory() {
        when(mockAuthFactory.supports("login")).thenReturn(true);
        Command mockCommand = mock(Command.class);
        when(mockAuthFactory.createCommand("login", mockClient)).thenReturn(mockCommand);

        Command result = commandFactory.createCommand("login", mockClient);

        assertNotNull(result);
        assertEquals(mockCommand, result);
    }

    @Test
    void testCreateCommandDelegatesToGameManagementFactory() {
        when(mockGameManagementFactory.supports("start-game")).thenReturn(true);
        Command mockCommand = mock(Command.class);
        when(mockGameManagementFactory.createCommand("start-game", mockClient)).thenReturn(mockCommand);

        Command result = commandFactory.createCommand("start-game", mockClient);

        assertNotNull(result);
        assertEquals(mockCommand, result);
    }

    @Test
    void testCreateCommandDelegatesToGameActionFactory() {
        when(mockGameActionFactory.supports("play-card")).thenReturn(true);
        Command mockCommand = mock(Command.class);
        when(mockGameActionFactory.createCommand("play-card", mockClient)).thenReturn(mockCommand);

        Command result = commandFactory.createCommand("play-card", mockClient);

        assertNotNull(result);
        assertEquals(mockCommand, result);
    }

    @Test
    void testCreateCommandDelegatesToGameInfoFactory() {
        when(mockGameInfoFactory.supports("show-score")).thenReturn(true);
        Command mockCommand = mock(Command.class);
        when(mockGameInfoFactory.createCommand("show-score", mockClient)).thenReturn(mockCommand);

        Command result = commandFactory.createCommand("show-score", mockClient);

        assertNotNull(result);
        assertEquals(mockCommand, result);
    }

@Test
    void testCreateCommandThrowsExceptionForUnknownCommand() {
        Exception exception = assertThrows(CommandNotFoundException.class, () -> commandFactory.createCommand("unknownCommand", mockClient));
        assertEquals("Unknown command: unknownCommand", exception.getMessage());
    }

    @Test
    void testCreateCommandThrowsExceptionForNullCommand() {
        Exception exception = assertThrows(CommandNotFoundException.class, () -> commandFactory.createCommand(null, mockClient));
    }
}
