package bg.sofia.uni.fmi.mjt.uno.command.factory;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.command.auth.LoginCommand;
import bg.sofia.uni.fmi.mjt.uno.command.auth.LogoutCommand;
import bg.sofia.uni.fmi.mjt.uno.command.auth.RegisterCommand;
import bg.sofia.uni.fmi.mjt.uno.exceptions.command.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.uno.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.player.account.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthCommandFactoryTest {

    private UserManager mockUserManager;
    private GameManager mockGameManager;
    private SocketChannel mockClient;
    private AuthCommandFactory authCommandFactory;

    @BeforeEach
    void setUp() {
        mockUserManager = mock(UserManager.class);
        mockGameManager = mock(GameManager.class);
        mockClient = mock(SocketChannel.class);

        authCommandFactory = new AuthCommandFactory(mockUserManager, mockGameManager);
    }

    @Test
    void testSupportsValidCommands() {
        assertTrue(authCommandFactory.supports("register"));
        assertTrue(authCommandFactory.supports("login"));
        assertTrue(authCommandFactory.supports("logout"));
    }

    @Test
    void testSupportsInvalidCommand() {
        assertFalse(authCommandFactory.supports("unknownCommand"));
    }

    @Test
    void testCreateCommandRegister() {
        Command command = authCommandFactory.createCommand("register", mockClient);
        assertInstanceOf(RegisterCommand.class, command);
    }

    @Test
    void testCreateCommandLogin() {
        Command command = authCommandFactory.createCommand("login", mockClient);
        assertInstanceOf(LoginCommand.class, command);
    }

    @Test
    void testCreateCommandLogout() {
        Command command = authCommandFactory.createCommand("logout", mockClient);
        assertInstanceOf(LogoutCommand.class, command);
    }

    @Test
    void testCreateCommandThrowsExceptionForUnknownCommand() {
        Exception exception = assertThrows(CommandNotFoundException.class, () -> authCommandFactory.createCommand("invalidCommand", mockClient));
        assertEquals("Unknown command: invalidCommand", exception.getMessage());
    }
}
