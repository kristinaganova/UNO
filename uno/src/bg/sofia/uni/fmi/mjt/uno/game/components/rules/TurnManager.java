package bg.sofia.uni.fmi.mjt.uno.game.components.rules;

import bg.sofia.uni.fmi.mjt.uno.exceptions.game.NoOnlinePlayersException;
import bg.sofia.uni.fmi.mjt.uno.game.components.GameMessenger;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.util.List;

public class TurnManager {

    private final List<Player> players;
    private int currentPlayerIndex;
    private boolean isReversed;
    private final GameMessenger gameMessenger;

    public TurnManager(List<Player> players, GameMessenger gameMessenger) {

        if (players == null || players.size() < 2) {
            throw new IllegalArgumentException("At least 2 players are required to start the game.");
        }

        this.players = players;
        this.currentPlayerIndex = 0;
        this.isReversed = false;
        this.gameMessenger = gameMessenger;

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

        if (players.stream().noneMatch(Player::isOnline)) {
            throw new NoOnlinePlayersException("No online players available to take a turn.");
        }

        do {
            currentPlayerIndex = isReversed
                    ? (currentPlayerIndex - 1 + players.size()) % players.size()
                    : (currentPlayerIndex + 1) % players.size();
        } while (!players.get(currentPlayerIndex).isOnline());

    }

    public void announceTurn() {
        gameMessenger.notifyAll("It is: " + getCurrentPlayer().getAccount().getUsername() + "'s turn.");
    }

    public void skipTurn() {
        advanceTurn();
        advanceTurn();
    }

    public void reverseDirection() {
        isReversed = !isReversed;
    }
}