package bg.sofia.uni.fmi.mjt.uno.games;

import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.game.GameState;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GameManager {

    private final Map<String, Game> games;
    private final GameStorage gameStorage;

    public GameManager() {
        this.games = new ConcurrentHashMap<>();
        this.gameStorage = new GameStorage();
        loadGames();
    }

    public synchronized boolean createGame(String gameId, int playersCount, Player creator) {
        if (games.containsKey(gameId)) {
            return false;
        }
        if (playersCount < Game.MIN_PLAYERS || playersCount > Game.MAX_PLAYERS) {
            throw new IllegalArgumentException("Number of players must be between " +
                    Game.MIN_PLAYERS + " and " + Game.MAX_PLAYERS);
        }
        Game game = new Game(gameId, playersCount, creator);
        games.put(gameId, game);
        saveGames();
        System.out.println("Game created with ID: " + gameId);
        return true;
    }

    public synchronized boolean joinGame(String gameId, Player player) {
        Game game = games.get(gameId);
        if (game == null || game.getGameState() != GameState.AVAILABLE) {
            throw new IllegalStateException("Game with ID " + gameId + " is not available.");
        }

        game.getPlayerRegistry().addPlayer(player);
        player.setGame(game);
        notifyPlayersInGame(game, player.getAccount().getUsername() + " has joined the game.");
        saveGames();
        return true;
    }

    public synchronized boolean startGame(String gameId, Player requestingPlayer) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new IllegalArgumentException("Game with this ID does not exist.");
        }

        game.startGame(requestingPlayer);
        saveGames();
        return true;
    }

    public void disconnectPlayer(String username) {
        Game game = getGameByPlayer(username);
        if (game != null) {
            game.disconnectPlayer(username);
            saveGames();
        }
    }

    public boolean reconnectPlayer(String username) {
        Game game = getGameByPlayer(username);
        if (game != null) {
            boolean reconnected = game.reconnectPlayer(username);
            saveGames();
            return reconnected;
        }
        return false;
    }

    public synchronized String getGamesByStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status cannot be null or blank.");
        }

        return games.values().stream()
                .filter(game -> {
                    switch (status.toLowerCase()) {
                        case "started":
                            return game.getGameState() == GameState.STARTED;
                        case "finished":
                            return game.getGameState() == GameState.FINISHED;
                        case "available":
                            return game.getGameState() == GameState.AVAILABLE;
                        case "all":
                            return true;
                        default:
                            throw new IllegalArgumentException("Invalid status: " + status);
                    }
                })
                .map(game -> String.format("Game ID: %s, Status: %s, Players: %d/%d",
                        game.getId(),
                        game.getGameState(),
                        game.getPlayerRegistry().getPlayers().size(),
                        game.getPlayerRegistry().getPlayers().size()))
                .collect(Collectors.joining("\n"));
    }

    public Game getGame(String gameId) {
        return games.get(gameId);
    }

    public boolean doesGameExist(String gameId) {
        return games.containsKey(gameId);
    }

    public Game getGameByPlayer(String username) {
        return games.values().stream()
                .filter(game -> game.getPlayerRegistry().getPlayers().stream()
                        .anyMatch(player -> player.getAccount().getUsername().equals(username)))
                .findFirst()
                .orElse(null);
    }

    public void saveGames() {
        gameStorage.saveGames(games);
    }

    public void loadGames() {
        Map<String, Game> loadedGames = gameStorage.loadGames();
        games.putAll(loadedGames);
        System.out.println("Games loaded successfully.");
    }

    private void notifyPlayersInGame(Game game, String message) {
        game.getGameMessenger().notifyAll(message);
    }

    public boolean isPlayerInAnyGame(String username) {
        return games.values().stream()
                .anyMatch(game -> game.getPlayerRegistry().getPlayers().stream()
                        .anyMatch(player -> player.getAccount().getUsername().equals(username)));
    }
}
