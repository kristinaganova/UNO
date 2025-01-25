package bg.sofia.uni.fmi.mjt.uno.game;

import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.util.Collections;
import java.util.List;

public class TurnManager {
    private final List<Player> players;
    private int currentPlayerIndex;
    private boolean isReversed;

    public TurnManager(List<Player> players) {
        if (players == null || players.size() < 2) {
            throw new IllegalArgumentException("At least 2 players are required to start the game.");
        }
        this.players = Collections.unmodifiableList(players);
        this.currentPlayerIndex = 0;
        this.isReversed = false;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public Player getNextPlayer() {
        int nextIndex = isReversed
                ? (currentPlayerIndex - 1 + players.size()) % players.size()
                : (currentPlayerIndex + 1) % players.size();
        return players.get(nextIndex);
    }

    public void advanceTurn() {
        currentPlayerIndex = isReversed
                ? (currentPlayerIndex - 1 + players.size()) % players.size()
                : (currentPlayerIndex + 1) % players.size();
    }

    public void skipTurn() {
        advanceTurn();
        advanceTurn();
    }

    public void reverseDirection() {
        isReversed = !isReversed;
    }

    public void removePlayer(Player player) {
        if (!players.contains(player)) {
            throw new IllegalArgumentException("Player not found in the game.");
        }
        if (players.size() <= 2) {
            throw new IllegalStateException("At least 2 players are required to continue the game.");
        }

        int removedIndex = players.indexOf(player);
        if (removedIndex < currentPlayerIndex || (removedIndex == currentPlayerIndex && isReversed)) {
            currentPlayerIndex = (currentPlayerIndex - 1 + players.size()) % players.size();
        }

        players.remove(player);
        currentPlayerIndex %= players.size();
    }
}
