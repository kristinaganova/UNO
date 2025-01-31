package bg.sofia.uni.fmi.mjt.uno.player;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.account.Account;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Player implements Serializable {
    private static final long serialVersionUID = -2246431532831884266L;

    private final Account account;
    private final Hand handManager;
    private SocketChannel client;
    private final AtomicBoolean unoCalled;
    private final AtomicBoolean isOnline;
    private volatile Game currentGame;

    public Player(Account account, SocketChannel client) {
        if (account == null || client == null) {
            throw new IllegalArgumentException("Account and client cannot be null.");
        }
        this.account = account;
        this.client = client;
        this.handManager = new Hand();
        this.unoCalled = new AtomicBoolean(false);
        this.isOnline = new AtomicBoolean(false);
    }

    public void sendMessage(String message) {
        if (client == null || !client.isOpen()) {
            System.err.println("[Player] Cannot send message to " + account.getUsername() + " (Client disconnected)");
            return;
        }

        try {
            ByteBuffer buffer = ByteBuffer.wrap((message + "\n").getBytes());
            client.write(buffer);
            System.out.println("[Player] Sent message to " + account.getUsername() + ": " + message);
        } catch (IOException e) {
            System.err.println("Error sending message to player " + account.getUsername() + ": " + e.getMessage());
        }
    }

    public boolean isOnline() {
        return isOnline.get();
    }

    public void setOnline(boolean online) {
        isOnline.set(online);
    }

    public SocketChannel getClient() {
        return client;
    }

    public Account getAccount() {
        return account;
    }

    public synchronized void setGame(Game game) {
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null.");
        }
        this.currentGame = game;
    }

    public void updateSocketChannel(SocketChannel newClient) {
        this.client = newClient;
    }

    public Game getCurrentGame() {
        Game game = this.currentGame;
        if (game == null) {
            throw new IllegalStateException("Player is not associated with any game.");
        }
        return game;
    }

    public synchronized void addCardToHand(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null.");
        }

        handManager.addCard(card);

        if (handManager.getHand().entrySet().size() > 1 && unoCalled.get()) {
            unoCalled.set(false);
        }
    }

    public synchronized boolean removeCardFromHand(Card card) {
        return handManager.removeCard(card);
    }

    public int getHandSize() {
        return handManager.getSize();
    }

    public void callUno() {
        if (getHandSize() == 1) {
            unoCalled.set(true);
        } else {
            throw new IllegalStateException("UNO can only be called with one card left.");
        }
    }

    public boolean hasCalledUno() {
        return unoCalled.get();
    }

    public String showHand() {
        List<Card> cards = handManager.getAllCards();
        if (cards.isEmpty()) {
            return "Your hand is empty.";
        }

        Game game = getCurrentGame();
        return "The top card is: " + game.getDeckHandler().getTopDiscardCard().getCardDescription() +
                System.lineSeparator() +
                "Current game color:  " + game.getDeckHandler().getCurrentColor().toString() +
                System.lineSeparator() +
                "Your hand: " +
                System.lineSeparator() +
                cards.stream()
                        .map(card -> card.getId() + " - " + card.getCardDescription())
                        .collect(Collectors.joining("\n"));
    }

    public Hand getHandManager() {
        return handManager;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Player player = (Player) obj;
        return Objects.equals(account, player.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(account);
    }

    public Hand getHand() {
        return handManager;
    }
}
