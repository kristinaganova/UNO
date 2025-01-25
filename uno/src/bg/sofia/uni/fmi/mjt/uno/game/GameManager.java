package bg.sofia.uni.fmi.mjt.uno.game;

import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GameManager {

    private final Map<String, Game> games;

    public GameManager() {
        this.games = new ConcurrentHashMap<>();
    }

    public synchronized boolean createGame(String gameId, int playersCount, Player creator) {
        if (games.containsKey(gameId)) {
            return false;
        }
        if (playersCount < Game.MIN_PLAYERS || playersCount > Game.MAX_PLAYERS) {
            throw new IllegalArgumentException("Number of players must be between " + Game.MIN_PLAYERS + " and " + Game.MAX_PLAYERS);
        }
        Game game = new Game(gameId, playersCount, creator);
        games.put(gameId, game);
        System.out.println("Game created with ID: " + gameId);
        return true;
    }

    public synchronized boolean joinGame(String gameId, Player player) {
        Game game = games.get(gameId);
        if (game == null || game.getState() != GameState.AVAILABLE) {
            throw new IllegalStateException("Game with ID " + gameId + " is not available.");
        }

        game.addPlayer(player);

        notifyPlayersInGame(game, player.getAccount().getUsername() + " has joined the game.");
        return true;
    }

    public synchronized boolean startGame(String gameId, Player requestingPlayer) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game with this ID does not exist.");
        }

        game.startGame(requestingPlayer);

        return true;
    }

    public synchronized String getGamesByStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status cannot be null or blank.");
        }

        return games.values().stream()
                .filter(game -> {
                    switch (status.toLowerCase()) {
                        case "started":
                            return game.getState() == GameState.STARTED;
                        case "finished":
                            return game.getState() == GameState.FINISHED;
                        case "available":
                            return game.getState() == GameState.AVAILABLE;
                        case "all":
                            return true;
                        default:
                            throw new IllegalArgumentException("Invalid status: " + status);
                    }
                })
                .map(game -> String.format("Game ID: %s, Status: %s, Players: %d/%d",
                        game.getId(), game.getState(), game.getPlayers().size(), game.getPlayersCount()))
                .collect(Collectors.joining("\n"));
    }

    public boolean doesGameExist(String gameId) {
        return games.containsKey(gameId);
    }

    public Game getGame(String gameId) {
        return games.get(gameId);
    }

    public boolean isPlayerInAnyGame(String username) {
        return games.values().stream()
                .anyMatch(game -> game.getPlayers().stream()
                        .anyMatch(player -> player.getAccount().getUsername().equals(username)));
    }

    private void notifyPlayersInGame(Game game, String message) {
        game.notifyPlayers(message);
    }

    public String getGameSummary(String gameId) {
        Game game = games.get(gameId);
        if (game == null) {
            return "Game not found.";
        }
        return String.format("Game ID: %s, Status: %s, Players: %d/%d",
                game.getId(), game.getState(), game.getPlayers().size(), game.getPlayersCount());
    }

    public Game getGameByPlayer(String username) {
        return games.values().stream()
                .filter(game -> game.getPlayers().stream()
                        .anyMatch(player -> player.getAccount().getUsername().equals(username)))
                .findFirst()
                .orElse(null);
    }

}
