package bg.sofia.uni.fmi.mjt.uno.server.command.auth;

import bg.sofia.uni.fmi.mjt.uno.server.exceptions.command.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.server.player.account.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegisterCommandTest {

    private UserManager mockUserManager;
    private RegisterCommand registerCommand;

    @BeforeEach
    void setUp() {
        mockUserManager = mock(UserManager.class);
        registerCommand = new RegisterCommand(mockUserManager);
    }

    @Test
    void testExecuteCommandSuccessfulRegistration() {
        String[] args = {"--username=testUser", "--password=securePass"};

        when(mockUserManager.createAccount("testUser", "securePass")).thenReturn(true);

        String result = registerCommand.executeCommand(args);

        assertEquals("User registered successfully.", result);
        verify(mockUserManager, times(1)).createAccount("testUser", "securePass");
    }

    @Test
    void testExecuteCommandUsernameAlreadyExists() {
        String[] args = {"--username=testUser", "--password=securePass"};

        when(mockUserManager.createAccount("testUser", "securePass")).thenReturn(false);

        Exception exception = assertThrows(CommandExecutionException.class, () -> registerCommand.executeCommand(args));
        assertEquals("Username already exists.", exception.getMessage());

        verify(mockUserManager, times(1)).createAccount("testUser", "securePass");
    }

    @Test
    void testExecuteCommandMissingArguments() {
        String[] args = {"--username=testUser"};

        Exception exception = assertThrows(CommandExecutionException.class, () -> registerCommand.executeCommand(args));
        assertEquals("Invalid arguments. Usage: register <username> <password>", exception.getMessage());
    }
}
