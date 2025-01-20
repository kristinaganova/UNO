package bg.sofia.uni.fmi.mjt.uno.player;

import bg.sofia.uni.fmi.mjt.uno.card.Card;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String name;
    private final List<Card> hand;
    private boolean unoCalled;
    private PlayerStatus status;

    public Player(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be null or empty");
        }
        this.name = name;
        this.hand = new ArrayList<>();
        this.unoCalled = false;
        this.status = PlayerStatus.ACTIVE;
    }

    public String getName() {
        return name;
    }

    public void addCardToHand(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        hand.add(card);
    }

    public boolean removeCardFromHand(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        return hand.remove(card);
    }

    public List<Card> getHand() {
        return new ArrayList<>(hand);
    }

    public boolean hasCalledUno() {
        return unoCalled;
    }

    public void callUno() {
        this.unoCalled = true;
    }

    public void resetUnoFlag() {
        this.unoCalled = false;
    }

    public boolean hasNoCards() {
        return hand.isEmpty();
    }

    public void showHand() {
        System.out.println(name + "'s hand:");
        for (Card card : hand) {
            System.out.println(card.getCardDescription());
        }
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }
}