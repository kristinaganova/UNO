package bg.sofia.uni.fmi.mjt.uno.player.account;

import java.io.Serializable;

public class Account implements Serializable {
    private static final long serialVersionUID = 6454870410141989623L;

    private final String username;
    private final String passwordHash;

    public Account(String username, String passwordHash) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank.");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be null or blank.");
        }
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public String toString() {
        return "Account{username='" + username + "'}";
    }
}
