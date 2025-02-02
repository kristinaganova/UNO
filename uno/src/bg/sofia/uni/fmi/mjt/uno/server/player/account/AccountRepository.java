package bg.sofia.uni.fmi.mjt.uno.server.player.account;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountRepository {
    private static final Path DEFAULT_PATH = Path.of("files", "users.dat");
    private static final String ERROR_SAVING = "Error saving accounts to file: ";
    private static final String ERROR_LOADING = "Error loading accounts from file: ";
    private static final String ERROR_DIRECTORY = "Error creating account directory: ";
    private static final String MESSAGE_ACCOUNTS_SAVED = "Accounts saved to file: ";
    private static final String MESSAGE_NO_ACCOUNTS_FILE = "No accounts file found. Returning empty map.";

    private final Path usersFilePath;

    public AccountRepository(Path usersFilePath) {
        this.usersFilePath = usersFilePath;
        ensureDirectoryExists();
    }

    public AccountRepository() {
        this(DEFAULT_PATH);
    }

    public void saveAccounts(Map<String, Account> accounts) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                Files.newOutputStream(usersFilePath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
            oos.writeObject(accounts);
            System.out.println(MESSAGE_ACCOUNTS_SAVED + usersFilePath);
        } catch (IOException e) {
            System.err.println(ERROR_SAVING + e.getMessage());
        }
    }

    public Map<String, Account> loadAccounts() {
        if (!Files.exists(usersFilePath)) {
            System.out.println(MESSAGE_NO_ACCOUNTS_FILE);
            return new ConcurrentHashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(usersFilePath))) {
            return (Map<String, Account>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(ERROR_LOADING + e.getMessage());
            return new ConcurrentHashMap<>();
        }
    }

    private void ensureDirectoryExists() {
        try {
            Path directory = usersFilePath.getParent();
            if (directory != null && !Files.exists(directory)) {
                Files.createDirectories(directory);
            }
        } catch (IOException e) {
            System.err.println(ERROR_DIRECTORY + e.getMessage());
        }
    }
}
