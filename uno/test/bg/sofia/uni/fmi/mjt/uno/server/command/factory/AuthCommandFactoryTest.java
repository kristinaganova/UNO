package bg.sofia.uni.fmi.mjt.uno.server.command.factory;

import bg.sofia.uni.fmi.mjt.uno.server.command.Command;
import bg.sofia.uni.fmi.mjt.uno.server.command.auth.LoginCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.auth.LogoutCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.auth.RegisterCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.factory.AuthCommandFactory;
import bg.sofia.uni.fmi.mjt.uno.server.exceptions.command.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.uno.server.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.server.player.account.UserManager;
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
        assertEquals("invalidCommand", exception.getMessage());
    }
}
