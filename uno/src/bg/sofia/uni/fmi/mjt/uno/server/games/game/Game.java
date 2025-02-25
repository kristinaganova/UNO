package bg.sofia.uni.fmi.mjt.uno.server.games.game;

import bg.sofia.uni.fmi.mjt.uno.server.exceptions.game.GameNotAvailableException;
import bg.sofia.uni.fmi.mjt.uno.server.exceptions.game.NotEnoughPlayersException;
import bg.sofia.uni.fmi.mjt.uno.server.exceptions.player.PlayerNotPermittedException;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.components.monitoring.GameMonitor;
import bg.sofia.uni.fmi.mjt.uno.server.loggers.CommandLogger;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.components.DeckHandler;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.components.GameMessenger;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.components.rules.GameRules;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.components.PlayerRegistry;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.components.rules.TurnManager;
import bg.sofia.uni.fmi.mjt.uno.server.loggers.CardLogger;
import bg.sofia.uni.fmi.mjt.uno.server.player.Player;

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
    private TurnManager turnManager;
    private final CardLogger logger;
    private GameState gameState;
    private final CommandLogger commandLogger;
    private GameMonitor gameMonitor;

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
        turnManager = new TurnManager(playerRegistry.getPlayers(), gameMessenger);
        gameState = GameState.AVAILABLE;
    }

    public void setGameState(GameState gameState) {
        if (gameState == null) {
            throw new IllegalArgumentException("Game state cannot be null.");
        }
        synchronized (this) {
            this.gameState = gameState;
        }
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
            gameMessenger.notifyAll("It is: " + turnManager.getCurrentPlayer().getAccount().username() + "'s turn.");
            setGameState(GameState.STARTED);

            gameMonitor = new GameMonitor(playerRegistry, gameMessenger);
            Thread monitorThread = new Thread(gameMonitor);
            monitorThread.start();
        }
    }

    private void distributeInitialCards(Player player) {
        StringBuilder message = new StringBuilder();

        for (int i = 0; i < INITIAL_CARDS; i++) {
            player.getHand().addCard(getDeckHandler().getDeck().drawCard());
        }
        message.append("Player ").append(player.getAccount().username())
                .append(" received initial cards.").append(System.lineSeparator());
        gameMessenger.notifyPlayer(player, "Your initial hand: " + player.showHand());

        gameMessenger.notifyAll(message.toString());
    }

    public synchronized void endGame() {
        stopGameMonitor();
        setGameState(GameState.FINISHED);
        List<Player> ranking = calculateFinalRanking();

        if (ranking.isEmpty()) {
            notifyNoRankedPlayers();
            return;
        }

        gameMessenger.notifyAll(getSummary());
        clearRemainingPlayers();
    }

    private synchronized void stopGameMonitor() {
        if (gameMonitor != null) {
            gameMonitor.stop();
        }
    }

    private List<Player> calculateFinalRanking() {
        return gameRules.calculateRanking();
    }

    private void notifyNoRankedPlayers() {
        gameMessenger.notifyAll("Game Over! But no players were ranked. Something went wrong.");
    }

    private void clearRemainingPlayers() {
        playerRegistry.getPlayers().clear();
    }

    public void logCommand(String command) {
        String playerName = turnManager.getCurrentPlayer().getAccount().username();

        String metadata = String.format("[Player: %s] [Time: %s] %s",
                playerName, LocalDateTime.now(), command);

        commandLogger.logCommand(metadata);
    }

    public synchronized void disconnectPlayer(String username) {
        Player player = findPlayerByUsername(username);

        if (player == null) {
            return;
        }

        turnManager.advanceTurn();

        player.setOnline(false);
        gameMessenger.notifyAll(username + " has disconnected.");

        if (turnManager != null) {
            turnManager.announceTurn();
        }
    }

    public synchronized boolean reconnectPlayer(String username) {
        Player player = findPlayerByUsername(username);

        if (player == null) {
            return false;
        }

        player.setOnline(true);
        getGameMessenger().notifyAll(username + " has reconnected.");
        player.sendMessage("It is: " + getTurnManager().getCurrentPlayer().getAccount().username() + "'s turn.");
        return true;
    }

    public String getSummary() {
        StringBuilder summary = new StringBuilder("Game Summary:" + System.lineSeparator());
        summary.append("Game ID: ").append(id).append(System.lineSeparator());
        summary.append("Creator: ").append(creator.getAccount().username()).append(System.lineSeparator());
        summary.append("State: ").append(gameState).append(System.lineSeparator());

        summary.append("Players:").append(System.lineSeparator());
        AtomicInteger rank = new AtomicInteger(1);
        playerRegistry.getPlayers().forEach(player -> summary.append(rank.getAndIncrement())
                .append(". ")
                .append(player.getAccount().username())
                .append(player.isOnline() ? " (Online)" : " (Offline)")
                .append(System.lineSeparator()));

        if (!playerRegistry.getFinishedPlayers().isEmpty()) {
            summary.append(System.lineSeparator()).append("Finished Players:").append(System.lineSeparator());
            rank.set(1);
            playerRegistry.getFinishedPlayers().forEach(player -> summary.append(rank.getAndIncrement())
                    .append(". ")
                    .append(player.getAccount().username())
                    .append(System.lineSeparator()));
        }

        return summary.toString();
    }

    public synchronized String leaveGame(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }

        String username = player.getAccount().username();
        gameMessenger.notifyAll("Player " + username + " has left the game.");

        if (isGameAvailableOrFinished()) {
            removePlayerFromGame(player);
            checkAndEndGame();
            return "Player " + username + " has left the game.";
        }

        return handleActiveGamePlayerLeave(player, username);
    }

    private void checkAndEndGame() {
        if (playerRegistry.getPlayers().isEmpty()) {
            endGame();
        }
    }

    private boolean isGameAvailableOrFinished() {
        return gameState == GameState.AVAILABLE || gameState == GameState.FINISHED;
    }

    private void removePlayerFromGame(Player player) {
        playerRegistry.removePlayer(player);
    }

    private String handleActiveGamePlayerLeave(Player player, String username) {
        boolean wasCurrentTurn = wasCurrentTurnPlayer(player);

        player.getHand().removeCards();
        removePlayerFromGame(player);

        if (shouldEndGame()) {
            gameMessenger.notifyAll("Not enough players left in the game. The game will now end.");
            endGame();
            return "Player " + username + " has left the game.";
        }

        if (wasCurrentTurn) {
            advanceTurn();
        }

        return "Player " + username + " has left the game.";
    }

    private boolean wasCurrentTurnPlayer(Player player) {
        return turnManager.getCurrentPlayer().equals(player);
    }

    private boolean shouldEndGame() {
        return playerRegistry.getPlayers().isEmpty() || playerRegistry.getPlayers().size() == 1;
    }

    public void advanceTurn() {
        turnManager.advanceTurn();
        turnManager.announceTurn();
    }

    private Player findPlayerByUsername(String username) {
        return playerRegistry.getPlayers().stream()
                .filter(p -> p.getAccount().username().equals(username))
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

    public GameMonitor getGameMonitor() {
        return gameMonitor;
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

    public TurnManager getTurnManager() {
        return turnManager;
    }
}