package bg.sofia.uni.fmi.mjt.uno.game.components;

import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.util.List;

public class GameMessenger {
    private final List<Player> players;

    public GameMessenger(List<Player> players) {
        this.players = players;
    }

    public void notifyAll(String message) {
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }

        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    public void notifyPlayer(Player player, String message) {
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
        if (player == null || !player.isOnline()) {
            throw new IllegalArgumentException("Player is not online");
        }

        System.out.println("[GameMessenger] Sending to " + player.getAccount().getUsername() + ": " + message);
        player.sendMessage("Server: " + message);
    }
}
