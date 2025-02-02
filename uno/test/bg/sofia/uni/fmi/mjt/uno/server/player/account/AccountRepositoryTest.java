package bg.sofia.uni.fmi.mjt.uno.server.player.account;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class AccountRepositoryTest {
    private Path testFilePath;
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() throws IOException {
        testFilePath = Files.createTempFile("test_users", ".dat");
        accountRepository = new AccountRepository(testFilePath);

        Files.deleteIfExists(testFilePath);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(testFilePath);
    }

    @Test
    void testSaveAndLoadAccounts() {
        Map<String, Account> accounts = new ConcurrentHashMap<>();
        accounts.put("Player1", new Account("Player1", "hashedPassword1"));
        accounts.put("Player2", new Account("Player2", "hashedPassword2"));

        accountRepository.saveAccounts(accounts);

        Map<String, Account> loadedAccounts = accountRepository.loadAccounts();

        assertNotNull(loadedAccounts, "Loaded accounts map should not be null.");
        assertEquals(2, loadedAccounts.size(), "Loaded accounts should contain 2 entries.");
        assertTrue(loadedAccounts.containsKey("Player1"), "Loaded accounts should contain Player1.");
        assertTrue(loadedAccounts.containsKey("Player2"), "Loaded accounts should contain Player2.");
        assertEquals("hashedPassword1", loadedAccounts.get("Player1").passwordHash(), "Player1's password should match.");
        assertEquals("hashedPassword2", loadedAccounts.get("Player2").passwordHash(), "Player2's password should match.");
    }

    @Test
    void testLoadAccountsWhenFileDoesNotExist() throws IOException {
        Files.deleteIfExists(testFilePath);

        Map<String, Account> accounts = accountRepository.loadAccounts();
        assertNotNull(accounts, "Returned map should not be null.");
        assertTrue(accounts.isEmpty(), "Returned map should be empty when no file exists.");
    }
}
