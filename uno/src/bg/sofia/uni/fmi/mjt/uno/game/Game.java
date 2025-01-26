package bg.sofia.uni.fmi.mjt.uno.game;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.deck.UnoDeck;
import bg.sofia.uni.fmi.mjt.uno.logging.CardLogger;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Game implements Serializable {
    private static final long serialVersionUID = -5799475912042338739L;

    public static final int MAX_PLAYERS = 10;
    public static final int MIN_PLAYERS = 2;
    public static final int INITIAL_CARDS = 7;

    private final String id;
    private final Player creator;
    private final List<Player> players;
    private final List<Player> finishedPlayers;
    private final UnoDeck deck;
    private TurnManager turnManager;
    private GameState state;
    private Color currentColor;
    private final CardLogger cardLogger;

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
        this.players = new ArrayList<>(playersCount);
        this.finishedPlayers = new LinkedList<>();
        this.deck = new UnoDeck();
        this.state = GameState.AVAILABLE;
        this.cardLogger = new CardLogger();

        addPlayer(creator);
    }

    public synchronized void startGame(Player requestingPlayer) {
        if (!creator.equals(requestingPlayer)) {
            throw new IllegalStateException("Only the game creator can start the game.");
        }
        if (state != GameState.AVAILABLE) {
            throw new IllegalStateException("Game is not available to start.");
        }
        if (players.size() < MIN_PLAYERS) {
            throw new IllegalStateException("At least 2 players are required to start the game.");
        }

        this.turnManager = new TurnManager(players);
        setFirstDiscardCard();
        distributeInitialCards();
        this.state = GameState.STARTED;

        notifyPlayers("The game has started!");
        notifyPlayersOfCurrentTurn();
    }

    private void setFirstDiscardCard() {
        Card firstCard = deck.drawCard();
        deck.discardCard(firstCard);
        currentColor = firstCard.getColor();
        firstCard.applyEffect(this);
        notifyPlayers("The base card is: " + firstCard.getCardDescription());
    }

    private void distributeInitialCards() {
        for (Player player : players) {
            for (int i = 0; i < INITIAL_CARDS; i++) {
                drawCard(player);
            }
            player.sendMessage("Your initial hand: " + player.showHand());
        }
    }

    public synchronized Card drawCard(Player player) {
        Card card = deck.drawCard();
        player.addCardToHand(card);
        player.sendMessage("You drew a card: " + card.getCardDescription());

        if (isGameOver()) {
            endGame();
            return card;
        }

        turnManager.advanceTurn();
        return card;
    }

    private boolean isGameOver() {
        int playersWithCards = 0;

        for (Player player : players) {
            if (!player.getHand().getAllCards().isEmpty()) {
                playersWithCards++;
            }
        }

        return playersWithCards == 1;
    }

    public synchronized void playerFinished(Player player) {
        if (!players.contains(player)) {
            throw new IllegalArgumentException("Player not found in the game.");
        }

        finishedPlayers.add(player);
        players.remove(player);
        notifyPlayers(player.getAccount().getUsername() + " has finished the game!");

        if (isGameOver()) {
            endGame();
        } else {
            notifyPlayersOfCurrentTurn();
        }
    }

    public synchronized void removePlayer(Player player) {
        if (!players.contains(player)) {
            throw new IllegalArgumentException("Player not found in the game.");
        }

        List<Card> handCards = player.getHand().getAllCards();
        for (Card card : handCards) {
            deck.discardCard(card);
        }

        players.remove(player);
        notifyPlayers(player.getAccount().getUsername() + " has left the game.");

        if (players.size() < MIN_PLAYERS) {
            state = GameState.FINISHED;
            notifyPlayers("Game has ended due to insufficient players.");
            return;
        }

        if (isGameOver()) {
            endGame();
        } else {
            turnManager.removePlayer(player);
            notifyPlayersOfCurrentTurn();
        }
    }

    private void endGame() {
        Player winner = null;
        for (Player player : players) {
            if (!player.getHand().getAllCards().isEmpty()) {
                winner = player;
                break;
            }
        }

        if (winner != null) {
            finishedPlayers.add(winner);
            notifyPlayers("The game is over! The winner is " + winner.getAccount().getUsername() + "!");
        } else {
            notifyPlayers("The game is over! It's a draw.");
        }

        state = GameState.FINISHED;
    }

    public Card getTopCard() {
        return deck.getTopDiscardCard();
    }

    public void notifyPlayers(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    private void notifyPlayersOfCurrentTurn() {
        Player currentPlayer = turnManager.getCurrentPlayer();
        notifyPlayers("It's " + currentPlayer.getAccount().getUsername() + "'s turn.");
        notifyPlayers("The top card is: " + getTopCard().getCardDescription());
    }

    public synchronized void addPlayer(Player player) {
        if (state != GameState.AVAILABLE) {
            throw new IllegalStateException("Cannot add players to a game that has already started or finished.");
        }
        if (players.contains(player)) {
            throw new IllegalStateException("Player is already in the game.");
        }
        players.add(player);
        player.getHand().removeCards();
        player.setGame(this);
        notifyPlayers(player.getAccount().getUsername() + " joined the game.");
    }

    public UnoDeck getDeck() {
        return deck;
    }

    public TurnManager getTurnManager() {
        return turnManager;
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    public GameState getState() {
        return state;
    }

    public String getId() {
        return id;
    }

    public Player getCreator() {
        return creator;
    }

    public List<Player> getPlayers() {
        return List.copyOf(players);
    }

    public List<Player> getFinishedPlayers() {
        return List.copyOf(finishedPlayers);
    }

    public Object getPlayersCount() {
        return players.size();
    }

    public CardLogger getCardLogger() {
        return cardLogger;
    }
}
