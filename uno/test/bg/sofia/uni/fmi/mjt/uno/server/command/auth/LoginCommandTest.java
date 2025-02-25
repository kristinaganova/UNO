package bg.sofia.uni.fmi.mjt.uno.server.command.auth;

import bg.sofia.uni.fmi.mjt.uno.server.command.auth.LoginCommand;
import bg.sofia.uni.fmi.mjt.uno.server.exceptions.command.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.Game;
import bg.sofia.uni.fmi.mjt.uno.server.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.server.player.account.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginCommandTest {

    private UserManager mockUserManager;
    private GameManager mockGameManager;
    private SocketChannel mockClient;
    private LoginCommand loginCommand;

    @BeforeEach
    void setUp() {
        mockUserManager = mock(UserManager.class);
        mockGameManager = mock(GameManager.class);
        mockClient = mock(SocketChannel.class);

        loginCommand = new LoginCommand(mockUserManager, mockGameManager, mockClient);
    }

    @Test
    void testExecuteCommandAlreadyLoggedIn() {
        String[] args = {"--username=testUser", "--password=securePass"};

        when(mockUserManager.isLoggedIn(mockClient)).thenReturn(true);

        Exception exception = assertThrows(CommandExecutionException.class, () -> loginCommand.executeCommand(args));
        assertEquals("You are already logged in.", exception.getMessage());

        verify(mockUserManager, never()).validateCredentials(anyString(), anyString());
        verify(mockUserManager, never()).login(any(), anyString());
    }

    @Test
    void testExecuteCommandInvalidCredentials() {
        String[] args = {"--username=testUser", "--password=wrongPass"};

        when(mockUserManager.isLoggedIn(mockClient)).thenReturn(false);
        when(mockUserManager.validateCredentials("testUser", "wrongPass")).thenReturn(false);

        Exception exception = assertThrows(CommandExecutionException.class, () -> loginCommand.executeCommand(args));
        assertEquals("Invalid username or password.", exception.getMessage());

        verify(mockUserManager, never()).login(any(), anyString());
    }

    @Test
    void testExecuteCommandMissingArguments() {
        String[] args = {"--username=testUser"};

        Exception exception = assertThrows(CommandExecutionException.class, () -> loginCommand.executeCommand(args));
        assertEquals("Invalid arguments. Usage: login --username=<username> --password=<password>", exception.getMessage());
    }

    @Test
    void testExecuteCommandSuccessfulLoginNoGame() {
        String[] args = {"--username=testUser", "--password=securePass"};

        when(mockUserManager.isLoggedIn(mockClient)).thenReturn(false);
        when(mockUserManager.validateCredentials("testUser", "securePass")).thenReturn(true);
        when(mockGameManager.getGameByPlayer("testUser")).thenReturn(null);

        String result = loginCommand.executeCommand(args);

        assertEquals("Login successful. No ongoing game found.", result);
        verify(mockUserManager).login(mockClient, "testUser");
    }

    @Test
    void testExecuteCommandSuccessfulLoginReconnectsToGame() {
        String[] args = {"--username=testUser", "--password=securePass"};
        Game mockGame = mock(Game.class);

        when(mockUserManager.isLoggedIn(mockClient)).thenReturn(false);
        when(mockUserManager.validateCredentials("testUser", "securePass")).thenReturn(true);
        when(mockGameManager.getGameByPlayer("testUser")).thenReturn(mockGame);
        when(mockGame.reconnectPlayer("testUser")).thenReturn(true);

        String result = loginCommand.executeCommand(args);

        assertEquals("Welcome back, testUser! You have been reconnected to your ongoing game.", result);
        verify(mockUserManager).login(mockClient, "testUser");
    }

    @Test
    void testExecuteCommandFailedToReconnect() {
        String[] args = {"--username=testUser", "--password=securePass"};
        Game mockGame = mock(Game.class);

        when(mockUserManager.isLoggedIn(mockClient)).thenReturn(false);
        when(mockUserManager.validateCredentials("testUser", "securePass")).thenReturn(true);
        when(mockGameManager.getGameByPlayer("testUser")).thenReturn(mockGame);
        when(mockGame.reconnectPlayer("testUser")).thenReturn(false);

        String result = loginCommand.executeCommand(args);

        assertEquals("Failed to reconnect you to your game.", result);
        verify(mockUserManager).login(mockClient, "testUser");
    }
}
