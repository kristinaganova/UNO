package bg.sofia.uni.fmi.mjt.uno.game.components;

import bg.sofia.uni.fmi.mjt.uno.exceptions.game.GameIsFullException;
import bg.sofia.uni.fmi.mjt.uno.exceptions.player.PlayerAlreadyInGameException;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerRegistry {

    private final List<Player> players;
    private final Map<Player, Boolean> finishedPlayersSpectators;
    private final int maxPlayers;

    public PlayerRegistry(int maxPlayers) {
        if (maxPlayers <= 0) {
            throw new IllegalArgumentException("maxPlayers must be greater than 0");
        }
        this.maxPlayers = maxPlayers;
        this.players = new CopyOnWriteArrayList<>();
        this.finishedPlayersSpectators = new ConcurrentHashMap<>();
    }

    private void validatePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("player cannot be null");
        }
    }

    public void addPlayer(Player player) {
        validatePlayer(player);

        if (players.contains(player)) {
            throw new PlayerAlreadyInGameException("Player is already in the game.");
        }
        if (players.size() >= maxPlayers) {
            throw new GameIsFullException("Cannot add more players. The game is full.");
        }
        player.setOnline(true);
        players.add(player);
    }

    public synchronized void removePlayer(Player player) {
        validatePlayer(player);

        if (!players.remove(player)) {
            throw new IllegalStateException("Player not found in the game.");
        }
    }

    public void markPlayerAsFinished(Player player) {
        validatePlayer(player);

        synchronized (this) {
            if (!players.contains(player)) {
                throw new IllegalStateException("Player is not in the game.");
            }
            finishedPlayersSpectators.put(player, false);
            players.remove(player);
        }
    }

    public void setPlayerAsSpectator(Player player) {
        validatePlayer(player);

        if (!finishedPlayersSpectators.containsKey(player)) {
            throw new IllegalStateException("Player has not finished the game.");
        }
        finishedPlayersSpectators.put(player, true);
    }

    public boolean isPlayerSpectator(Player player) {
        validatePlayer(player);

        return finishedPlayersSpectators.getOrDefault(player, false);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getFinishedPlayers() {
        return new ArrayList<>(finishedPlayersSpectators.keySet());
    }

    public boolean hasEnoughPlayers() {
        return players.size() >= 2;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}