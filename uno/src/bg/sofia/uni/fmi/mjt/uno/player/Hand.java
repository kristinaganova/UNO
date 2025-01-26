package bg.sofia.uni.fmi.mjt.uno.player;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Hand {
    private final Map<Color, List<Card>> hand;

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
        System.out.println("Adding card: " + card.getCardDescription() + " with color: " + card.getColor());
        hand.get(card.getColor()).add(card);
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

    public String showHand() {
        return hand.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
    }

    public  Map<Color, List<Card>> getHand() {
        return hand;
    }

    public void removeCards() {
        for (Color color : hand.keySet()) {
            hand.put(color, new ArrayList<>());
        }
    }
}
