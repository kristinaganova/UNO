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
    private final List<Player> players;
    private final TurnManager turnManager;
    private final UnoDeck deck;
    private GameState state;
    private final int playersCount;
    private Color currentColor;

    public Game(String id, int playersCount) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Game ID cannot be null or empty.");
        }
        if (playersCount < MIN_PLAYERS || playersCount > MAX_PLAYERS) {
            throw new IllegalArgumentException("Number of players must be between " +
                    MIN_PLAYERS + " and " + MAX_PLAYERS);
        }

        this.id = id;
        this.playersCount = playersCount;
        this.players = new ArrayList<>(playersCount);
        this.turnManager = new TurnManager(players);
        this.deck = new UnoDeck();
        this.state = GameState.CREATED;
    }

    public String getId() {
        return id;
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
        if (state != GameState.CREATED) {
            throw new IllegalStateException("Cannot add players to a game that has already started or finished.");
        }
        if (players.size() >= MAX_PLAYERS) {
            throw new IllegalStateException("The game is full. Maximum players: " + MAX_PLAYERS);
        }
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }

        players.add(player);
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

    public void setGameState(GameState state) {
        if (state == null) {
            throw new IllegalArgumentException("Game state cannot be null.");
        }
        this.state = state;
    }

    public TurnManager getTurnManager() {
        return turnManager;
    }

    public Card drawCard(Player player) {
        Card card = deck.drawCard();
        player.addCardToHand(card);
        return card;
    }

    public boolean playCard(Player player, Card card) {
        Card topCard = deck.getTopDiscardCard();

        if (!card.isPlayable(topCard)) {
            return false;
        }

        player.removeCardFromHand(card);

        deck.discardCard(card);

        card.applyEffect(this);

        return true;
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }
}