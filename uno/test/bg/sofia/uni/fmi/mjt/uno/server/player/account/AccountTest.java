package bg.sofia.uni.fmi.mjt.uno.server.player.account;

import bg.sofia.uni.fmi.mjt.uno.server.player.account.Account;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void testAccountCreationSuccess() {
        String username = "Player1";
        String passwordHash = "hashedPassword123";

        Account account = new Account(username, passwordHash);

        assertEquals(username, account.username(), "Username should match the input value.");
        assertEquals(passwordHash, account.passwordHash(), "Password hash should match the input value.");
    }

    @Test
    void testAccountCreationWithNullUsernameThrowsException() {
        String passwordHash = "hashedPassword123";

        assertThrows(IllegalArgumentException.class, () -> new Account(null, passwordHash),
                "Creating an account with a null username should throw IllegalArgumentException.");
    }

    @Test
    void testAccountCreationWithBlankUsernameThrowsException() {
        String passwordHash = "hashedPassword123";

        assertThrows(IllegalArgumentException.class, () -> new Account(" ", passwordHash),
                "Creating an account with a blank username should throw IllegalArgumentException.");
    }

    @Test
    void testAccountCreationWithNullPasswordHashThrowsException() {
        String username = "Player1";

        assertThrows(IllegalArgumentException.class, () -> new Account(username, null),
                "Creating an account with a null password hash should throw IllegalArgumentException.");
    }

    @Test
    void testAccountCreationWithBlankPasswordHashThrowsException() {
        String username = "Player1";

        assertThrows(IllegalArgumentException.class, () -> new Account(username, " "),
                "Creating an account with a blank password hash should throw IllegalArgumentException.");
    }

    @Test
    void testToStringMethod() {
        Account account = new Account("Player1", "hashedPassword123");

        String expected = "Account{username='Player1'}";
        assertEquals(expected, account.toString(), "toString method should return the correct format.");
    }
}
