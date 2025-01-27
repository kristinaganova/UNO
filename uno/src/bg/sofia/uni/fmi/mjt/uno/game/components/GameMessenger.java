package bg.sofia.uni.fmi.mjt.uno.game.components;

import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.util.List;

public class GameMessenger {
    private final List<Player> players;

    public GameMessenger(List<Player> players) {
        this.players = players;
    }

    public void notifyAll(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    public void notifyPlayer(Player player, String message) {
        player.sendMessage(message);
    }
}
