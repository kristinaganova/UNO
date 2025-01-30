package bg.sofia.uni.fmi.mjt.uno.loggers;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Collections;
import java.util.List;

public class CardLogger implements Serializable {
    private static final long serialVersionUID = 6785347667368644663L;

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
        StringBuilder summary = new StringBuilder("Last Played Cards (up to 10):").append(System.lineSeparator());
        for (Card card : playedCards) {
            summary.append(card.getCardDescription()).append(System.lineSeparator());
        }
        return summary.toString();
    }
}
