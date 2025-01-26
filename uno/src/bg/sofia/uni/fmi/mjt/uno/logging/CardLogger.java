package bg.sofia.uni.fmi.mjt.uno.logging;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;

import java.util.LinkedList;
import java.util.Collections;
import java.util.List;

public class CardLogger {

    private static final int MAX_CARDS = 10;
    private final LinkedList<Card> playedCards;

    public CardLogger() {
        this.playedCards = new LinkedList<>();
    }

    public void logCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null.");
        }
        if (playedCards.size() == MAX_CARDS) {
            playedCards.removeFirst();
        }
        playedCards.addLast(card);
    }

    public List<Card> getPlayedCards() {
        return Collections.unmodifiableList(playedCards);
    }

    public String getLogSummary() {
        StringBuilder summary = new StringBuilder("Last Played Cards (up to 10):\n");
        for (Card card : playedCards) {
            summary.append(card.getCardDescription()).append("\n");
        }
        return summary.toString();
    }
}
