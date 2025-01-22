package bg.sofia.uni.fmi.mjt.uno.game;

import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.util.List;

public class TurnManager {
    private final List<Player> players;
    private int currentPlayerIndex;
    private boolean isReversed;

    public TurnManager(List<Player> players) {
        if (players == null || players.size() < 2) {
            throw new IllegalArgumentException("At least 2 players are required to start the game.");
        }
        this.players = players;
        this.currentPlayerIndex = 0;
        this.isReversed = false;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void advanceTurn() {
        if (isReversed) {
            currentPlayerIndex = (currentPlayerIndex - 1 + players.size()) % players.size();
        } else {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
    }

    public void skipTurn() {
        advanceTurn();
        advanceTurn();
    }

    public void reverseDirection() {
        isReversed = !isReversed;
    }

    public Player getNextPlayer() {
        if (isReversed) {
            return players.get((currentPlayerIndex - 1 + players.size()) % players.size());
        } else {
            return players.get((currentPlayerIndex + 1) % players.size());
        }
    }

    public void removePlayer(Player player) {
        if (!players.contains(player)) {
            throw new IllegalArgumentException("Player not found in the game.");
        }

        int playerIndex = players.indexOf(player);

        if (playerIndex == currentPlayerIndex) {
            advanceTurn();
        } else if (playerIndex < currentPlayerIndex) {
            currentPlayerIndex--;
        }

        players.remove(player);
        if (players.size() < 2) {
            throw new IllegalStateException("At least 2 players are required to continue the game.");
        }

        currentPlayerIndex = currentPlayerIndex % players.size();
    }

    public void printTurnOrder() {
        System.out.println("Current turn order (isReversed: " + isReversed + "):");
        int index = currentPlayerIndex;
        for (int i = 0; i < players.size(); i++) {
            System.out.println(players.get(index).getAccount().getUsername());
            index = isReversed ? (index - 1 + players.size()) % players.size() : (index + 1) % players.size();
        }
    }
}
