package bg.sofia.uni.fmi.mjt.uno.player.account;

import bg.sofia.uni.fmi.mjt.uno.player.Player;
import org.junit.jupiter.api.*;

import java.nio.channels.SocketChannel;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserManagerTest {
    private UserManager userManager;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        // Reset singleton instance before each test to avoid shared state
        Field instanceField = UserManager.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);

        userManager = UserManager.getInstance();
    }

    @Test
    void testCreateDuplicateAccountFails() {
        userManager.createAccount("testUser", "password123");
        assertFalse(userManager.createAccount("testUser", "newPassword"),
                "Creating an account with the same username should fail.");
    }

    @Test
    void testCreateAccountWithInvalidDataThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> userManager.createAccount(null, "password"),
                "Should throw exception for null username.");
        assertThrows(IllegalArgumentException.class, () -> userManager.createAccount("user", null),
                "Should throw exception for null password.");
        assertThrows(IllegalArgumentException.class, () -> userManager.createAccount("", "password"),
                "Should throw exception for blank username.");
    }

    @Test
    void testLoginSuccessfully() {
        userManager.createAccount("testUser", "password123");
        SocketChannel mockClient = mock(SocketChannel.class);

        assertTrue(userManager.login(mockClient, "testUser"),
                "User should be able to log in successfully.");
    }

    @Test
    void testLoginTwiceWithSameUser() {
        userManager.createAccount("testUser", "password123");
        SocketChannel client1 = mock(SocketChannel.class);
        SocketChannel client2 = mock(SocketChannel.class);

        assertTrue(userManager.login(client1, "testUser"),
                "First login should succeed.");

        Exception exception = assertThrows(IllegalStateException.class, () -> userManager.login(client2, "testUser"));
        assertEquals("User is already logged in from another session.", exception.getMessage(),
                "Should throw exception if the same user tries to log in from another session.");
    }

    @Test
    void testLoginWithInvalidUserThrowsException() {
        SocketChannel mockClient = mock(SocketChannel.class);
        assertThrows(IllegalArgumentException.class, () -> userManager.login(mockClient, "nonexistentUser"),
                "Should throw exception for invalid username.");
    }

    @Test
    void testLogoutSuccessfully() {
        userManager.createAccount("testUser", "password123");
        SocketChannel mockClient = mock(SocketChannel.class);
        userManager.login(mockClient, "testUser");

        assertTrue(userManager.logout(mockClient), "User should be able to log out.");
        assertFalse(userManager.isLoggedIn(mockClient), "User should not be logged in after logout.");
    }

    @Test
    void testLogoutNonLoggedInUserFails() {
        SocketChannel mockClient = mock(SocketChannel.class);
        assertFalse(userManager.logout(mockClient), "Logging out a non-logged-in user should fail.");
    }

    @Test
    void testValidateCredentials() {
        userManager.createAccount("testUser", "password123");

        assertTrue(userManager.validateCredentials("testUser", "password123"),
                "Correct credentials should be validated.");
        assertFalse(userManager.validateCredentials("testUser", "wrongPassword"),
                "Incorrect password should not be validated.");
        assertFalse(userManager.validateCredentials("nonexistentUser", "password123"),
                "Nonexistent users should not pass validation.");
    }

    @Test
    void testGetLoggedInUsername() {
        userManager.createAccount("testUser", "password123");
        SocketChannel mockClient = mock(SocketChannel.class);
        userManager.login(mockClient, "testUser");

        assertEquals("testUser", userManager.getLoggedInUsername(mockClient),
                "Should return the correct logged-in username.");
    }

    @Test
    void testGetLoggedInUser() {
        userManager.createAccount("testUser", "password123");
        SocketChannel mockClient = mock(SocketChannel.class);
        userManager.login(mockClient, "testUser");

        Account account = userManager.getLoggedInUser(mockClient);
        assertNotNull(account, "Logged-in user should not be null.");
        assertEquals("testUser", account.getUsername(),
                "Logged-in account should match the expected username.");
    }

    @Test
    void testGetOrCreatePlayer() {
        userManager.createAccount("testUser", "password123");
        SocketChannel mockClient = mock(SocketChannel.class);

        Player player = userManager.getOrCreatePlayer("testUser", mockClient);
        assertNotNull(player, "Player should be created or retrieved successfully.");
        assertEquals("testUser", player.getAccount().getUsername(),
                "Created player should have the correct username.");
    }

    @Test
    void testGetOrCreatePlayerThrowsForInvalidUser() {
        SocketChannel mockClient = mock(SocketChannel.class);
        assertThrows(IllegalStateException.class, () -> userManager.getOrCreatePlayer("nonexistentUser", mockClient),
                "Fetching a player for a nonexistent user should throw an exception.");
    }

    @Test
    void testIsLoggedIn() {
        userManager.createAccount("testUser", "password123");
        SocketChannel mockClient = mock(SocketChannel.class);

        assertFalse(userManager.isLoggedIn(mockClient),
                "User should not be logged in initially.");
        userManager.login(mockClient, "testUser");
        assertTrue(userManager.isLoggedIn(mockClient),
                "User should be logged in after successful login.");
    }
}
