package bg.sofia.uni.fmi.mjt.uno.game;

import bg.sofia.uni.fmi.mjt.uno.exceptions.game.GameNotAvailableException;
import bg.sofia.uni.fmi.mjt.uno.exceptions.game.NotEnoughPlayersException;
import bg.sofia.uni.fmi.mjt.uno.exceptions.player.PlayerNotPermittedException;
import bg.sofia.uni.fmi.mjt.uno.game.components.monitoring.GameMonitor;
import bg.sofia.uni.fmi.mjt.uno.loggers.CommandLogger;
import bg.sofia.uni.fmi.mjt.uno.game.components.DeckHandler;
import bg.sofia.uni.fmi.mjt.uno.game.components.GameMessenger;
import bg.sofia.uni.fmi.mjt.uno.game.components.rules.GameRules;
import bg.sofia.uni.fmi.mjt.uno.game.components.PlayerRegistry;
import bg.sofia.uni.fmi.mjt.uno.game.components.rules.TurnManager;
import bg.sofia.uni.fmi.mjt.uno.loggers.CardLogger;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {
    public static final int MAX_PLAYERS = 10;
    public static final int MIN_PLAYERS = 2;
    private static final int INITIAL_CARDS = 7;

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
        this.gameMessenger = new GameMessenger(playerRegistry);
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
        if (gameState == null) {
            throw new IllegalArgumentException("Game state cannot be null.");
        }
        this.gameState = gameState;
    }

    public void startGame(Player requestingPlayer) {

        if (requestingPlayer == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }

        if (!creator.equals(requestingPlayer)) {
            throw new PlayerNotPermittedException("Only the game creator can start the game.");
        }

        if (!playerRegistry.hasEnoughPlayers()) {
            throw new NotEnoughPlayersException("At least 2 players are required to start the game.");
        }

        synchronized (this) {
            if (gameState != GameState.AVAILABLE) {
                throw new GameNotAvailableException("Game has already started or is not available.");
            }

            turnManager = new TurnManager(playerRegistry.getPlayers(), gameMessenger);
            gameMessenger.notifyAll("The game has started!");
            deckHandler.getTopDiscardCard().applyEffect(this);
            gameMessenger.notifyAll("It is: " + turnManager.getCurrentPlayer().getAccount().getUsername() + "'s turn.");
            setGameState(GameState.STARTED);

            gameMonitor = new GameMonitor(playerRegistry, gameMessenger);
            Thread monitorThread = new Thread(gameMonitor);
            monitorThread.start();
        }
    }

    private void distributeInitialCards(Player player) {
        StringBuilder message = new StringBuilder();

        for (int i = 0; i < INITIAL_CARDS; i++) {
            deckHandler.drawCard(player);
        }
        message.append("Player ").append(player.getAccount().getUsername())
                .append(" received initial cards.").append(System.lineSeparator());
        gameMessenger.notifyPlayer(player, "Your initial hand: " + player.showHand());

        gameMessenger.notifyAll(message.toString());
    }

    public synchronized void endGame() {
        if (gameMonitor != null) {
            gameMonitor.stop();
        }
        setGameState(GameState.FINISHED);

        List<Player> ranking = gameRules.calculateRanking();
        StringBuilder summary = new StringBuilder("Game Over! Final Ranking:" + System.lineSeparator());
        AtomicInteger rank = new AtomicInteger(1);
        ranking.forEach(player -> summary.append(rank.getAndIncrement())
                .append(". ")
                .append(player.getAccount().getUsername())
                .append(player.getHand().getAllCards().isEmpty() ? " (Finished)" : " - Cards left: "
                        + player.getHand().getAllCards().size())
                .append(System.lineSeparator()));

        gameMessenger.notifyAll(summary.toString());
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

    public synchronized void disconnectPlayer(String username) {

        Player player = findPlayerByUsername(username);

        if (player != null) {
            if (getTurnManager().getCurrentPlayer() == player) {
                getTurnManager().advanceTurn();
            }
            player.setOnline(false);
            getGameMessenger().notifyAll(username + " has disconnected.");
            getTurnManager().announceTurn();
        }

    }

    public synchronized boolean reconnectPlayer(String username) {

        Player player = findPlayerByUsername(username);

        if (player == null) {
            return false;
        }

        player.setOnline(true);
        getGameMessenger().notifyAll(username + " has reconnected.");
        player.sendMessage("It is: " + getTurnManager().getCurrentPlayer().getAccount().getUsername() + "'s turn.");
        return true;

    }

    public String getSummary() {

        StringBuilder summary = new StringBuilder("Game Summary:" + System.lineSeparator());
        summary.append("Game ID: ").append(id).append(System.lineSeparator());
        summary.append("Creator: ").append(creator.getAccount().getUsername()).append(System.lineSeparator());
        summary.append("State: ").append(gameState).append(System.lineSeparator());

        summary.append("Players:" + System.lineSeparator());
        AtomicInteger rank = new AtomicInteger(1);
        playerRegistry.getPlayers().forEach(player -> summary.append(rank.getAndIncrement())
                .append(". ")
                .append(player.getAccount().getUsername())
                .append(player.isOnline() ? " (Online)" : " (Offline)")
                .append(System.lineSeparator()));

        if (!playerRegistry.getFinishedPlayers().isEmpty()) {
            summary.append(System.lineSeparator()).append("Finished Players:").append(System.lineSeparator());
            rank.set(1);
            playerRegistry.getFinishedPlayers().forEach(player -> summary.append(rank.getAndIncrement())
                    .append(". ")
                    .append(player.getAccount().getUsername())
                    .append(System.lineSeparator()));
        }

        return summary.toString();

    }

    public synchronized String leaveGame(Player player) {

        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }

        player.getHand().removeCards();

        playerRegistry.removePlayer(player);
        gameMessenger.notifyAll("Player " + player.getAccount().getUsername() + " has left the game.");

        if (playerRegistry.getPlayers().isEmpty() || playerRegistry.getPlayers().size() == 1) {
            gameMessenger.notifyAll("Not enough players left in the game. The game will now end.");
            endGame();
        }

        return "Player " + player.getAccount().getUsername() + " has left the game.";

    }

    private Player findPlayerByUsername(String username) {

        return playerRegistry.getPlayers().stream()
                .filter(p -> p.getAccount().getUsername().equals(username))
                .findFirst()
                .orElse(null);

    }

    public void joinGame(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
        getPlayerRegistry().addPlayer(player);
        player.setGame(this);
        distributeInitialCards(player);
    }

    public CommandLogger getCommandLogger() {
        return commandLogger;
    }
}
