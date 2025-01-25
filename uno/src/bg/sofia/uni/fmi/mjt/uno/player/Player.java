package bg.sofia.uni.fmi.mjt.uno.player;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.logging.CardLogger;
import bg.sofia.uni.fmi.mjt.uno.player.account.Account;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Player {
    private final Account account;
    private final Hand handManager;
    private final CardLogger cardLogger;
    private final SocketChannel client;
    private boolean unoCalled;
    private Game currentGame;

    public Player(Account account, SocketChannel client) {
        if (account == null || client == null) {
            throw new IllegalArgumentException("Account and client cannot be null.");
        }
        this.account = account;
        this.client = client;
        this.handManager = new Hand();
        this.cardLogger = new CardLogger();
        this.unoCalled = false;
    }

    public void sendMessage(String message) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap((message + "\n").getBytes());
            client.write(buffer);
        } catch (IOException e) {
            System.err.println("Error sending message to player " + account.getUsername() + ": " + e.getMessage());
        }
    }

    public SocketChannel getClient() {
        return client;
    }

    public CardLogger getCardLogger() {
        return cardLogger;
    }

    public Account getAccount() {
        return account;
    }

    public String getInput() {
        try {
            client.configureBlocking(false);
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int bytesRead = client.read(buffer);

            if (bytesRead > 0) {
                buffer.flip();
                return new String(buffer.array(), 0, buffer.limit()).trim();
            } else if (bytesRead == 0) {
                return null;
            } else {
                throw new IOException("Client disconnected.");
            }
        } catch (IOException e) {
            System.err.println("Error reading input from player " + account.getUsername() + ": " + e.getMessage());
            return null;
        }
    }

    public void setGame(Game game) {
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null.");
        }
        this.currentGame = game;
    }

    public Game getCurrentGame() {
        return this.currentGame;
    }

    public void addCardToHand(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null.");
        }
        handManager.addCard(card);
        System.out.println("Card added to " + account.getUsername() + "'s hand: " + card.getCardDescription());
        unoCalled = false;
    }

    public boolean removeCardFromHand(Card card) {
        boolean removed = handManager.removeCard(card);
        if (getHandSize() == 1 && !unoCalled) {
            System.out.println(account.getUsername() + " forgot to call UNO!");
        }
        return removed;
    }

    public int getHandSize() {
        return handManager.getSize();
    }

    public void callUno() {
        if (getHandSize() == 1) {
            unoCalled = true;
        } else {
            throw new IllegalStateException("UNO can only be called with one card left.");
        }
    }

    public boolean hasCalledUno() {
        return unoCalled;
    }

    public String showHand() {
        List<Card> cards = handManager.getAllCards();
        if (cards.isEmpty()) {
            return "Your hand is empty.";
        }
        return cards.stream()
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
}