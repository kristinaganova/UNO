package bg.sofia.uni.fmi.mjt.uno.server.player;

import bg.sofia.uni.fmi.mjt.uno.server.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Hand {
    private final Map<Color, List<Card>> hand;
    private Optional<Card> lastDrawnCard;

    public Hand() {
        this.hand = new HashMap<>();
        for (Color color : Color.values()) {
            hand.put(color, new ArrayList<>());
        }
    }

    public void addCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null.");
        }
        hand.get(card.getColor()).add(card);
    }

    public Card getCardById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Card ID cannot be null or empty.");
        }

        return getAllCards().stream()
                .filter(card -> id.equals(card.getId()))
                .toList().getFirst();
    }

    public boolean removeCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null.");
        }
        return hand.get(card.getColor()).remove(card);
    }

    public List<Card> getAllCards() {
        System.out.println("Current hand content: " + hand);
        return hand.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public int getSize() {
        return getAllCards().size();
    }

    public  Map<Color, List<Card>> getHand() {
        return hand;
    }

    public void removeCards() {
        for (Color color : hand.keySet()) {
            hand.put(color, new ArrayList<>());
        }
    }

    public Optional<Card> getLastDrawnCard() {
        return lastDrawnCard;
    }

    public void setLastDrawnCard(Card lastDrawnCard) {
        this.lastDrawnCard = Optional.ofNullable(lastDrawnCard);
    }
}
