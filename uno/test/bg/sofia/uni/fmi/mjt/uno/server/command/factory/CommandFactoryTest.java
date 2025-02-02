package bg.sofia.uni.fmi.mjt.uno.server.command.factory;

import bg.sofia.uni.fmi.mjt.uno.server.command.Command;
import bg.sofia.uni.fmi.mjt.uno.server.exceptions.command.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.server.exceptions.command.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.uno.server.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.server.player.account.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandFactoryTest {
    private CommandFactory commandFactory;
    private UserManager mockUserManager;
    private GameManager mockGameManager;
    private SocketChannel mockClient;

    @BeforeEach
    void setUp() {
        mockUserManager = mock(UserManager.class);
        mockGameManager = mock(GameManager.class);
        mockClient = mock(SocketChannel.class);

        commandFactory = new CommandFactory(mockUserManager, mockGameManager);
    }

    @Test
    void testCreateCommandWithValidAuthCommand() {
        AuthCommandFactory mockAuthFactory = mock(AuthCommandFactory.class);
        Command mockCommand = mock(Command.class);

        when(mockAuthFactory.supports("login")).thenReturn(true);
        when(mockAuthFactory.createCommand("login", mockClient)).thenReturn(mockCommand);

        CommandFactory factory = new CommandFactory(mockUserManager, mockGameManager);
        Command result = factory.createCommand("login", mockClient);

        assertNotNull(result, "Command should not be null for a valid command.");
    }

    @Test
    void testCreateCommandWithInvalidCommandThrowsException() {
        assertThrows(CommandNotFoundException.class,
                () -> commandFactory.createCommand("invalid_command", mockClient),
                "Unknown commands should throw CommandNotFoundException.");
    }

    @Test
    void testCreateCommandWithNullCommandNameThrowsException() {
        assertThrows(CommandNotFoundException.class,
                () -> commandFactory.createCommand(null, mockClient),
                "Null command name should throw CommandNotFoundException.");
    }

    @Test
    void testCreateCommandWithEmptyCommandNameThrowsException() {
        assertThrows(CommandNotFoundException.class,
                () -> commandFactory.createCommand("", mockClient),
                "Empty command name should throw CommandNotFoundException.");
    }

    @Test
    void testCreateCommandWithNullClientThrowsException() {
        assertThrows(CommandExecutionException.class,
                () -> commandFactory.createCommand("login", null),
                "Null client should throw CommandExecutionException.");
    }

    @Test
    void testConstructorWithNullUserManagerThrowsException() {
        assertThrows(CommandExecutionException.class,
                () -> new CommandFactory(null, mockGameManager),
                "Null UserManager should throw CommandExecutionException.");
    }

    @Test
    void testConstructorWithNullGameManagerThrowsException() {
        assertThrows(CommandExecutionException.class,
                () -> new CommandFactory(mockUserManager, null),
                "Null GameManager should throw CommandExecutionException.");
    }
}
