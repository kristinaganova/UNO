package bg.sofia.uni.fmi.mjt.uno.player.account;

import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {
    private final Map<String, Account> accounts;
    private final Map<SocketChannel, String> loggedInUsers;
    private final Map<Account, Player> accountToPlayerMap;

    public UserManager() {
        this.accounts = AccountRepository.loadAccounts();
        this.loggedInUsers = new ConcurrentHashMap<>();
        this.accountToPlayerMap = new ConcurrentHashMap<>();
    }

    public synchronized boolean createAccount(String username, String plainPassword) {
        if (username == null || plainPassword == null || username.isBlank() || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Username and password cannot be null or blank.");
        }

        String hashedPassword = PasswordUtils.hashPassword(plainPassword);
        Account newAccount = new Account(username, hashedPassword);

        if (accounts.putIfAbsent(username, newAccount) == null) {
            AccountRepository.saveAccounts(accounts);
            return true;
        }

        return false;
    }

    public synchronized boolean login(SocketChannel client, String username) {
        if (isLoggedIn(client)) {
            throw new IllegalStateException("Client is already logged in.");
        }

        if (!accounts.containsKey(username)) {
            throw new IllegalArgumentException("Invalid username.");
        }

        if (loggedInUsers.containsValue(username)) {
            throw new IllegalStateException("User is already logged in from another session.");
        }

        loggedInUsers.put(client, username);

        Account account = accounts.get(username);
        accountToPlayerMap.putIfAbsent(account, new Player(account, client));

        return true;
    }

    public synchronized Player getPlayerByUsername(String username) {
        Account account = accounts.get(username);
        if (account == null) {
            throw new IllegalStateException("Account for username " + username + " does not exist.");
        }

        Player player = accountToPlayerMap.get(account);
        if (player == null) {
            throw new IllegalStateException("Player instance for username " + username + " does not exist.");
        }

        return player;
    }

    public synchronized Player getOrCreatePlayer(String username, SocketChannel client) {
        Account account = accounts.get(username);
        if (account == null) {
            throw new IllegalStateException("Account for username " + username + " does not exist.");
        }

        return accountToPlayerMap.computeIfAbsent(account, acc -> new Player(acc, client));
    }

    public synchronized boolean validateCredentials(String username, String plainPassword) {
        Account account = accounts.get(username);
        return account != null && PasswordUtils.verifyPassword(plainPassword, account.getPasswordHash());
    }

    public synchronized boolean isLoggedIn(SocketChannel client) {
        return loggedInUsers.containsKey(client);
    }

    public synchronized boolean logout(SocketChannel client) {
        String username = loggedInUsers.remove(client);
        if (username != null) {
            System.out.println("User " + username + " logged out.");
            return true;
        }
        return false;
    }

    public synchronized String getLoggedInUsername(SocketChannel client) {
        return loggedInUsers.get(client);
    }

    public synchronized Account getLoggedInUser(SocketChannel client) {
        return accounts.get(loggedInUsers.get(client));
    }
}
