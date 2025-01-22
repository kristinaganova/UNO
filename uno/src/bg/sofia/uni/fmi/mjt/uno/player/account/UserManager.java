package bg.sofia.uni.fmi.mjt.uno.player.account;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {
    private final ConcurrentHashMap<String, Account> accounts;
    private final ConcurrentHashMap<SocketChannel, String> loggedInUsers;

    public UserManager() {
        this.accounts = new ConcurrentHashMap<>();
        this.loggedInUsers = new ConcurrentHashMap<>();
    }

    public synchronized boolean createAccount(String username, String plainPassword) {
        if (username == null || plainPassword == null || username.isBlank() || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Username and password cannot be null or blank.");
        }

        String hashedPassword = PasswordUtils.hashPassword(plainPassword);
        Account newAccount = new Account(username, hashedPassword);

        return accounts.putIfAbsent(username, newAccount) == null;
    }

    public synchronized boolean validateCredentials(String username, String plainPassword) {
        Account account = accounts.get(username);
        return account != null && PasswordUtils.verifyPassword(plainPassword, account.getPasswordHash());
    }

    public synchronized boolean isLoggedIn(SocketChannel client) {
        return loggedInUsers.containsKey(client);
    }

    public synchronized boolean login(SocketChannel client, String username) {
        if (isLoggedIn(client)) {
            throw new IllegalStateException("Client is already logged in.");
        }

        if (!accounts.containsKey(username)) {
            throw new IllegalArgumentException("Invalid username.");
        }

        loggedInUsers.put(client, username);
        return true;
    }

    public synchronized boolean logout(SocketChannel client) {
        return loggedInUsers.remove(client) != null;
    }

    public synchronized String getLoggedInUsername(SocketChannel client) {
        return loggedInUsers.get(client);
    }

    public synchronized Account getLoggedInUser(SocketChannel client) {
        return accounts.get(loggedInUsers.get(client));
    }
}
