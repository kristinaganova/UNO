package bg.sofia.uni.fmi.mjt.uno.player.account;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class AccountRepositoryTest {
    private static final String TEST_USERS_FILE = "test_users.dat";
    private AccountRepository accountRepository;
    private final File testFile = new File(TEST_USERS_FILE);

    @BeforeEach
    void setUp() {
        accountRepository = new AccountRepository(TEST_USERS_FILE);
        if (testFile.exists()) {
            assertTrue(testFile.delete(), "Test file should be deleted before each test.");
        }
    }

    @AfterEach
    void tearDown() {
        if (testFile.exists()) {
            assertTrue(testFile.delete(), "Test file should be deleted after each test.");
        }
    }

    @Test
    void testSaveAndLoadAccounts() {
        Map<String, Account> accounts = new ConcurrentHashMap<>();
        accounts.put("Player1", new Account("Player1", "hashedPassword1"));
        accounts.put("Player2", new Account("Player2", "hashedPassword2"));

        // Save accounts
        accountRepository.saveAccounts(accounts);

        // Load accounts
        Map<String, Account> loadedAccounts = accountRepository.loadAccounts();

        assertNotNull(loadedAccounts, "Loaded accounts map should not be null.");
        assertEquals(2, loadedAccounts.size(), "Loaded accounts should contain 2 entries.");
        assertTrue(loadedAccounts.containsKey("Player1"), "Loaded accounts should contain Player1.");
        assertTrue(loadedAccounts.containsKey("Player2"), "Loaded accounts should contain Player2.");
        assertEquals("hashedPassword1", loadedAccounts.get("Player1").getPasswordHash(), "Player1's password should match.");
        assertEquals("hashedPassword2", loadedAccounts.get("Player2").getPasswordHash(), "Player2's password should match.");
    }

    @Test
    void testLoadAccountsWhenFileDoesNotExist() {
        if (testFile.exists()) {
            assertTrue(testFile.delete(), "Test file should be deleted before this test.");
        }

        Map<String, Account> accounts = accountRepository.loadAccounts();
        assertNotNull(accounts, "Returned map should not be null.");
        assertTrue(accounts.isEmpty(), "Returned map should be empty when no file exists.");
    }
}
