package bg.sofia.uni.fmi.mjt.uno.deck;

import bg.sofia.uni.fmi.mjt.uno.card.Card;
import bg.sofia.uni.fmi.mjt.uno.card.Color;
import bg.sofia.uni.fmi.mjt.uno.card.StandardCard;
import bg.sofia.uni.fmi.mjt.uno.card.actioncard.ActionCard;
import bg.sofia.uni.fmi.mjt.uno.card.actioncard.ActionCardType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> drawPile;
    private final List<Card> discardPile;

    private static final int MAX_BLACK_COUNT = 4;
    private static final int MAX_VALUE = 9;

    public Deck() {
        drawPile = new ArrayList<>();
        discardPile = new ArrayList<>();
        initializeDeck();
        shuffleDeck();
    }

    private void initializeDeck() {
        createStandardCards();
        createActionCards();
    }

    private void createStandardCards() {
        for (Color color : Color.values()) {
            if (color == Color.BLACK) continue;

            drawPile.add(new StandardCard(color, 0));

            for (int i = 1; i <= MAX_VALUE; i++) {
                drawPile.add(new StandardCard(color, i));
                drawPile.add(new StandardCard(color, i));
            }
        }
    }

    private void createActionCards() {
        for (Color color : Color.values()) {
            if (color == Color.BLACK) {
                continue;
            }

            drawPile.add(new ActionCard(ActionCardType.PLUS_TWO, color));
            drawPile.add(new ActionCard(ActionCardType.PLUS_TWO, color));

            drawPile.add(new ActionCard(ActionCardType.SKIP, color));
            drawPile.add(new ActionCard(ActionCardType.SKIP, color));

            drawPile.add(new ActionCard(ActionCardType.REVERSE, color));
            drawPile.add(new ActionCard(ActionCardType.REVERSE, color));
        }

        for (int i = 0; i < MAX_BLACK_COUNT; i++) {
            drawPile.add(new ActionCard(ActionCardType.PICK_COLOR, Color.BLACK));
            drawPile.add(new ActionCard(ActionCardType.PLUS_FOUR, Color.BLACK));
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(drawPile);
    }

    public Card drawCard() {
        if (drawPile.isEmpty()) {
            replenishDrawPile();
        }
        return drawPile.isEmpty() ? null : drawPile.remove(drawPile.size() - 1);
    }

    public void discardCard(Card card) {
        discardPile.add(card);
    }

    private void replenishDrawPile() {
        if (discardPile.isEmpty()) {
            return;
        }

        Card topCard = discardPile.remove(discardPile.size() - 1);
        drawPile.addAll(discardPile);
        discardPile.clear();
        discardPile.add(topCard);

        shuffleDeck();
    }

    public Card getTopDiscardCard() {
        return discardPile.isEmpty() ? null : discardPile.get(discardPile.size() - 1);
    }

    public int getRemainingCards() {
        return drawPile.size();
    }
}