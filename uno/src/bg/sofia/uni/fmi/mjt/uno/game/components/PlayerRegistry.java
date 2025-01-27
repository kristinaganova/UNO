package bg.sofia.uni.fmi.mjt.uno.game.components;

import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PlayerRegistry {
    private final List<Player> players;
    private final Map<Player, Boolean> finishedPlayersSpectators; // Map за завършили играчи и дали са наблюдатели
    private final int maxPlayers;

    public PlayerRegistry(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        this.players = new ArrayList<>(maxPlayers);
        this.finishedPlayersSpectators = new LinkedHashMap<>();
    }

    public void addPlayer(Player player) {
        if (players.contains(player)) {
            throw new IllegalStateException("Player is already in the game.");
        }
        if (players.size() >= maxPlayers) {
            throw new IllegalStateException("Cannot add more players. The game is full.");
        }
        player.setOnline(true);
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
        player.setOnline(false);
    }

    public void markPlayerAsFinished(Player player) {
        if (!players.contains(player)) {
            throw new IllegalStateException("Player is not in the game.");
        }
        players.remove(player);
        finishedPlayersSpectators.put(player, false);
    }

    public void setPlayerAsSpectator(Player player) {
        if (!finishedPlayersSpectators.containsKey(player)) {
            throw new IllegalStateException("Player has not finished the game.");
        }
        finishedPlayersSpectators.put(player, true);
    }

    public boolean isPlayerSpectator(Player player) {
        return finishedPlayersSpectators.getOrDefault(player, false);
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public List<Player> getFinishedPlayers() {
        return new ArrayList<>(finishedPlayersSpectators.keySet());
    }

    public boolean hasEnoughPlayers() {
        return players.size() >= 2;
    }
}
