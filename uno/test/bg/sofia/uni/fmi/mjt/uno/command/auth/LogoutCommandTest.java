package bg.sofia.uni.fmi.mjt.uno.command.auth;

import bg.sofia.uni.fmi.mjt.uno.player.account.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LogoutCommandTest {

    private UserManager mockUserManager;
    private SocketChannel mockClient;
    private LogoutCommand logoutCommand;

    @BeforeEach
    void setUp() {
        mockUserManager = mock(UserManager.class);
        mockClient = mock(SocketChannel.class);

        logoutCommand = new LogoutCommand(mockUserManager, mockClient);
    }

    @Test
    void testExecuteWhenUserIsNotLoggedIn() {
        when(mockUserManager.isLoggedIn(mockClient)).thenReturn(false);

        String result = logoutCommand.execute(new String[]{});

        assertEquals("You are not logged in.", result);
        verify(mockUserManager, never()).logout(any());
    }

    @Test
    void testExecuteWhenUserIsLoggedIn() {
        when(mockUserManager.isLoggedIn(mockClient)).thenReturn(true);

        String result = logoutCommand.execute(new String[]{});

        assertEquals("Logout successful.", result);
        verify(mockUserManager, times(1)).logout(mockClient);
    }
}
