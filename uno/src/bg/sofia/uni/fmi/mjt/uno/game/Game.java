package bg.sofia.uni.fmi.mjt.uno.game;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.deck.UnoDeck;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Game implements Serializable {
    private static final long serialVersionUID = -5799475912042338739L;

    public static final int MAX_PLAYERS = 10;
    public static final int MIN_PLAYERS = 2;
    public static final int START_HAND_CARDS_COUNT = 7;

    private final String id;
    private final Player creator;
    private final List<Player> players;
    private TurnManager turnManager;
    private final UnoDeck deck;
    private GameState state;
    private final int playersCount;
    private Color currentColor;

    public Game(String id, int playersCount, Player creator) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Game ID cannot be null or empty.");
        }
        if (playersCount < MIN_PLAYERS || playersCount > MAX_PLAYERS) {
            throw new IllegalArgumentException("Number of players must be between " +
                    MIN_PLAYERS + " and " + MAX_PLAYERS);
        }
        if (creator == null) {
            throw new IllegalArgumentException("Creator cannot be null.");
        }

        this.id = id;
        this.playersCount = playersCount;
        this.creator = creator;
        this.players = new ArrayList<>(playersCount);
        this.deck = new UnoDeck();
        this.state = GameState.AVAILABLE;
        this.turnManager = null;

        addPlayer(creator);
    }

    public String getId() {
        return id;
    }

    public Player getCreator() {
        return creator;
    }

    public GameState getState() {
        return state;
    }

    public int getPlayersCount() {
        return playersCount;
    }

    public List<Player> getPlayers() {
        return List.copyOf(players);
    }

    public UnoDeck getDeck() {
        return deck;
    }

    public void addPlayer(Player player) {
        if (state != GameState.AVAILABLE) {
            throw new IllegalStateException("Cannot add players to a game that has already started or finished.");
        }
        if (players.size() >= playersCount) {
            throw new IllegalStateException("The game is full. Maximum players: " + playersCount);
        }
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }

        players.add(player);
        player.sendMessage("You have joined the game: " + id);
    }

    public void removePlayer(Player player) {
        if (!players.contains(player)) {
            throw new IllegalArgumentException("Player not found in the game.");
        }

        players.remove(player);

        if (players.size() < MIN_PLAYERS) {
            state = GameState.FINISHED;
        }
    }

    public synchronized boolean startGame(Player requestingPlayer) {
        if (isCreator(requestingPlayer)) {
            throw new IllegalStateException("Only the creator of the game can start it.");
        }

        if (getState() != GameState.AVAILABLE) {
            throw new IllegalStateException("Game cannot be started. Current state: " + getState());
        }

        if (getPlayers().size() < Game.MIN_PLAYERS) {
            throw new IllegalStateException("At least " + Game.MIN_PLAYERS + " players are required to start the game.");
        }

        distributeInitialCards();
        setFirstDiscardCard();
        setGameState(GameState.STARTED);
        initializeTurnManager();
        notifyPlayers("The game has started!");

        return true;
    }

    private void initializeTurnManager() {
        if (turnManager == null) {
            turnManager = new TurnManager(players);
        }
        System.out.println(turnManager.getCurrentPlayer().getAccount().getUsername());
    }

    private void distributeInitialCards() {
        for (Player player : players) {
            for (int i = 0; i < START_HAND_CARDS_COUNT; i++) {
                drawCard(player);
            }
        }
    }

    private void setFirstDiscardCard() {
        Card firstCard = deck.drawCard();

        deck.discardCard(firstCard);
        firstCard.applyEffect(this);
        currentColor = firstCard.getColor();
    }

    public void notifyPlayersOfGameState() {
        if (turnManager == null) {
            throw new IllegalStateException("The game has not started yet. TurnManager is not initialized.");
        }

        for (Player player : players) {
            player.sendMessage("The game has started!");
            Card topDiscardCard = deck.getTopDiscardCard();
            player.sendMessage("Top discard card: " +
                    (topDiscardCard != null ? topDiscardCard.getCardDescription() : "No card played yet."));
            player.sendMessage("It's " + turnManager.getCurrentPlayer().getAccount().getUsername() + "'s turn!");
        }
    }

    public Card drawCard(Player player) {
        Card card = deck.drawCard();
        player.addCardToHand(card);
        player.sendMessage("You drew a card: " + card.getCardDescription());
        return card;
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public void notifyPlayers(String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be null or blank.");
        }

        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    public TurnManager getTurnManager() {
        return turnManager;
    }

    public boolean isCreator(Player requestingPlayer) {
        return creator.equals(requestingPlayer);
    }

    public void setGameState(GameState gameState) {
        if (gameState == null) {
            return;
        }
        this.state = gameState;
    }
}
