package bg.sofia.uni.fmi.mjt.uno.game.components;

import bg.sofia.uni.fmi.mjt.uno.player.Player;

public class GameMessenger {
    private final PlayerRegistry playerRegistry;

    public GameMessenger(PlayerRegistry playerRegistry) {

        this.playerRegistry = playerRegistry;
    }

    public void notifyAll(String message) {
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }

        for (Player player : playerRegistry.getPlayers()) {
            player.sendMessage(message);
        }

        for (Player player : playerRegistry.getFinishedPlayers()) {
            player.sendMessage(message);
        }
    }

    public void notifySpectators(String message) {
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }

        for (Player player : playerRegistry.getPlayers()) {
            if (playerRegistry.isPlayerSpectator(player)) {
                player.sendMessage(message);
            }
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
