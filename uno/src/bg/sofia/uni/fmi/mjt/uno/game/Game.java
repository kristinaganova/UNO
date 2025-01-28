package bg.sofia.uni.fmi.mjt.uno.game;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.command.CommandLogger;
import bg.sofia.uni.fmi.mjt.uno.game.components.DeckHandler;
import bg.sofia.uni.fmi.mjt.uno.game.components.GameMessenger;
import bg.sofia.uni.fmi.mjt.uno.game.components.GameRules;
import bg.sofia.uni.fmi.mjt.uno.game.components.PlayerRegistry;
import bg.sofia.uni.fmi.mjt.uno.game.components.TurnManager;
import bg.sofia.uni.fmi.mjt.uno.logging.CardLogger;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Game implements Serializable {
    public static final int MAX_PLAYERS = 10;
    public static final int MIN_PLAYERS = 2;
    private static final int INITIAL_CARDS = 7;

    private static final long serialVersionUID = -4015997933952996744L;

    private final String id;
    private final Player creator;
    private final DeckHandler deckHandler;
    private final PlayerRegistry playerRegistry;
    private final GameRules gameRules;
    private final GameMessenger gameMessenger;
    private TurnManager turnManager = null;
    private final CardLogger logger;
    private GameState gameState;
    private final CommandLogger commandLogger;
    private transient GameMonitor gameMonitor;

    public Game(String id, int playersCount, Player creator) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Game ID cannot be null or blank.");
        }
        if (playersCount < MIN_PLAYERS || playersCount > MAX_PLAYERS) {
            throw new IllegalArgumentException("Players count must be between 2 and 10.");
        }
        if (creator == null) {
            throw new IllegalArgumentException("Creator cannot be null.");
        }

        this.id = id;
        this.creator = creator;
        this.deckHandler = new DeckHandler();
        this.playerRegistry = new PlayerRegistry(playersCount);
        this.gameRules = new GameRules(playerRegistry);
        this.gameMessenger = new GameMessenger(playerRegistry.getPlayers());
        this.logger = new CardLogger();
        this.commandLogger = new CommandLogger(id);
        gameState = GameState.AVAILABLE;
    }

    public String getId() {
        return id;
    }

    public Player getCreator() {
        return creator;
    }

    public DeckHandler getDeckHandler() {
        return deckHandler;
    }

    public PlayerRegistry getPlayerRegistry() {
        return playerRegistry;
    }

    public GameRules getGameRules() {
        return gameRules;
    }

    public GameMessenger getGameMessenger() {
        return gameMessenger;
    }

    public GameState getGameState() {
        return gameState;
    }

    public CardLogger getLogger() {
        return logger;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public synchronized void startGame(Player requestingPlayer) {
        if (!creator.equals(requestingPlayer)) {
            throw new IllegalStateException("Only the game creator can start the game.");
        }
        if (!playerRegistry.hasEnoughPlayers()) {
            throw new IllegalStateException("At least 2 players are required to start the game.");
        }

        turnManager = new TurnManager(playerRegistry.getPlayers());
        distributeInitialCards();
        gameMessenger.notifyAll("The game has started!");
        setGameState(GameState.STARTED);

        gameMonitor = new GameMonitor(playerRegistry, gameMessenger);
        Thread monitorThread = new Thread(gameMonitor);
        monitorThread.start();
    }

    private void distributeInitialCards() {
        for (Player player : playerRegistry.getPlayers()) {
            for (int i = 0; i < INITIAL_CARDS; i++) {
                Card card = deckHandler.drawCard(player);
            }
            gameMessenger.notifyPlayer(player, "Your initial hand: " + player.showHand());
        }
    }

    public synchronized void endGame() {
        if (gameMonitor != null) {
            gameMonitor.stop();
        }

        List<Player> ranking = gameRules.calculateRanking(null);

        StringBuilder summary = new StringBuilder("Game Over! Final Ranking:\n");
        AtomicInteger rank = new AtomicInteger(1);
        ranking.forEach(player -> summary.append(rank.getAndIncrement())
                .append(". ")
                .append(player.getAccount().getUsername())
                .append(player.getHand().getAllCards().isEmpty() ?
                        " (Finished)" : " - Cards left: " + player.getHand().getAllCards().size())
                .append("\n"));

        gameMessenger.notifyAll(summary.toString());
        setGameState(GameState.FINISHED);
    }

    public TurnManager getTurnManager() {
        return turnManager;
    }

    public void logCommand(String command) {
        String metadata = String.format("[Player: %s] [Time: %s] %s",
                turnManager.getCurrentPlayer().getAccount().getUsername(),
                LocalDateTime.now(),
                command);
        commandLogger.logCommand(metadata);
    }

    public void disconnectPlayer(String username) {

        Player player = getPlayerRegistry().getPlayers().stream()
                .filter(p -> p.getAccount().getUsername().equals(username))
                .findFirst()
                .orElse(null);
        if (player != null) {
            player.setOnline(false);
            getGameMessenger().notifyAll(username + " has disconnected.");
        }

    }

    public boolean reconnectPlayer(String username) {
        Player player = getPlayerRegistry().getPlayers().stream()
                .filter(p -> p.getAccount().getUsername().equals(username))
                .findFirst()
                .orElse(null);

        if (player == null) {
            return false;
        }

        player.setOnline(true);
        getGameMessenger().notifyAll(username + " has reconnected.");
        return true;
    }

    public String getSummary() {
        StringBuilder summary = new StringBuilder("Game Summary:\n");
        summary.append("Game ID: ").append(id).append("\n");
        summary.append("Creator: ").append(creator.getAccount().getUsername()).append("\n");
        summary.append("State: ").append(gameState).append("\n");

        summary.append("Players:\n");
        AtomicInteger rank = new AtomicInteger(1);
        playerRegistry.getPlayers().forEach(player -> summary.append(rank.getAndIncrement())
                .append(". ")
                .append(player.getAccount().getUsername())
                .append(player.isOnline() ? " (Online)" : " (Offline)")
                .append("\n"));

        if (!playerRegistry.getFinishedPlayers().isEmpty()) {
            summary.append("\nFinished Players:\n");
            rank.set(1);
            playerRegistry.getFinishedPlayers().forEach(player -> summary.append(rank.getAndIncrement())
                    .append(". ")
                    .append(player.getAccount().getUsername())
                    .append("\n"));
        }

        return summary.toString();
    }

    public synchronized String leaveGame(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }

        List<Card> hand = player.getHandManager().getAllCards();
        if (!hand.isEmpty()) {
            for (Card card : hand) {
                deckHandler.discardCard(card);
            }
        }

        playerRegistry.removePlayer(player);
        gameMessenger.notifyAll("Player " + player.getAccount().getUsername() + " has left the game.");

        if (playerRegistry.getPlayers().isEmpty()) {
            gameMessenger.notifyAll("No players left in the game. The game will now end.");
            endGame();
        }

        return "Player " + player.getAccount().getUsername() + " has left the game.";
    }
}
