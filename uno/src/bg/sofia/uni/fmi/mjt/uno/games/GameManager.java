package bg.sofia.uni.fmi.mjt.uno.games;

import bg.sofia.uni.fmi.mjt.uno.exceptions.game.GameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.uno.exceptions.game.GameNotAvailableException;
import bg.sofia.uni.fmi.mjt.uno.exceptions.game.GameNotFoundException;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.game.GameState;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GameManager {
    private static volatile GameManager instance;

    private final Map<String, Game> games;

    private GameManager() {
        this.games = new ConcurrentHashMap<>();
    }

    public static GameManager getInstance() {
        if (instance == null) {
            synchronized (GameManager.class) {
                if (instance == null) {
                    instance = new GameManager();
                }
            }
        }
        return instance;
    }

    public boolean createGame(String gameId, int playersCount, Player creator) {
        if (gameId == null || gameId.isEmpty()) {
            throw new IllegalArgumentException("Game ID cannot be null or empty.");
        }
        if (creator == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
        if (playersCount < Game.MIN_PLAYERS || playersCount > Game.MAX_PLAYERS) {
            throw new IllegalArgumentException("Number of players must be between " +
                    Game.MIN_PLAYERS + " and " + Game.MAX_PLAYERS);
        }

        synchronized (this) {
            if (games.containsKey(gameId)) {
                throw new GameAlreadyExistsException("Game with ID: " + gameId + " already exists.");
            }
            games.put(gameId, new Game(gameId, playersCount, creator));
        }

        System.out.println("Game created with ID: " + gameId);
        return true;
    }

    public boolean joinGame(String gameId, Player player) {
        if (gameId == null || gameId.isEmpty()) {
            throw new IllegalArgumentException("Game ID cannot be null or empty.");
        }
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }

        Game game = getGame(gameId);
        if (game == null || game.getGameState() == GameState.FINISHED) {
            throw new GameNotAvailableException("Game with ID " + gameId + " is not available.");
        }

        game.getPlayerRegistry().addPlayer(player);
        player.setGame(game);
        notifyPlayersInGame(game, player.getAccount().getUsername() + " has joined the game.");
        return true;
    }

    public boolean startGame(String gameId, Player requestingPlayer) {
        if (gameId == null || gameId.isEmpty()) {
            throw new IllegalArgumentException("Game ID cannot be null or empty.");
        }
        if (requestingPlayer == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }

        Game game = getGame(gameId);
        if (game == null) {
            throw new GameNotFoundException("Game with this ID does not exist.");
        }

        game.startGame(requestingPlayer);
        return true;
    }

    public String getGamesByStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status cannot be null or blank.");
        }

        return games.values().stream()
                .filter(game -> switch (status.toLowerCase()) {
                    case "started" -> game.getGameState() == GameState.STARTED;
                    case "finished" -> game.getGameState() == GameState.FINISHED;
                    case "available" -> game.getGameState() == GameState.AVAILABLE;
                    case "all" -> true;
                    default -> throw new IllegalArgumentException("Invalid status: " + status);
                })
                .map(game -> String.format("Game ID: %s, Status: %s, Players: %d/%d",
                        game.getId(),
                        game.getGameState(),
                        game.getPlayerRegistry().getPlayers().size(),
                        game.getPlayerRegistry().getMaxPlayers()))
                .collect(Collectors.joining(System.lineSeparator()));
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
                        .anyMatch(player -> player.getAccount() != null &&
                                player.getAccount().getUsername().equals(username)))
                .findFirst()
                .orElse(null);
    }

    protected void notifyPlayersInGame(Game game, String message) {
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null.");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be null or blank.");
        }
        game.getGameMessenger().notifyAll(message);
    }

    public boolean isPlayerInAnyGame(String username) {
        return games.values().stream()
                .anyMatch(game -> game.getPlayerRegistry().getPlayers().stream()
                        .anyMatch(player -> player.getAccount() != null &&
                                player.getAccount().getUsername().equals(username)));
    }

    public void removeGame(String gameId) {
        if (gameId == null || gameId.isEmpty()) {
            throw new IllegalArgumentException("Game ID cannot be null or empty.");
        }

        synchronized (this) {
            if (games.remove(gameId) != null) {
                System.out.println("Game with ID: " + gameId + " has been removed.");
            } else {
                System.out.println("Game with ID: " + gameId + " was not found.");
            }
        }
    }

    public synchronized void clearGames() {
        games.clear();
    }

}