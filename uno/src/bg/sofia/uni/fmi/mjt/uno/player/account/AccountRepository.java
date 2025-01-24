package bg.sofia.uni.fmi.mjt.uno.player.account;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountRepository {

    private static final String USERS_FILE = "users.dat";

    public static void saveAccounts(Map<String, Account> accounts) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(accounts);
            System.out.println("Accounts saved to file: " + USERS_FILE);
        } catch (IOException e) {
            System.err.println("Error saving accounts to file: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Account> loadAccounts() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            System.out.println("No accounts file found. Starting fresh.");
            return new ConcurrentHashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<String, Account>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading accounts from file: " + e.getMessage());
            return new ConcurrentHashMap<>();
        }
    }
}
