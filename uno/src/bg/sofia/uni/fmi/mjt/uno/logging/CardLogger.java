package bg.sofia.uni.fmi.mjt.uno.logging;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardLogger {

    private final List<Card> playedCards;

    public CardLogger() {
        this.playedCards = new ArrayList<>();
    }

    public void logCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null.");
        }
        playedCards.add(card);
    }

    public List<Card> getPlayedCards() {
        return Collections.unmodifiableList(playedCards);
    }

    public String getLogSummary() {
        StringBuilder summary = new StringBuilder("Played Cards:\n");
        for (Card card : playedCards) {
            summary.append(card.getCardDescription()).append("\n");
        }
        return summary.toString();
    }
}