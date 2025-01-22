package bg.sofia.uni.fmi.mjt.uno.deck;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.card.models.StandardCard;
import bg.sofia.uni.fmi.mjt.uno.card.models.ActionCard;
import bg.sofia.uni.fmi.mjt.uno.card.types.ActionCardType;
import bg.sofia.uni.fmi.mjt.uno.card.models.WildCard;
import bg.sofia.uni.fmi.mjt.uno.card.types.WildCardType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnoDeck implements Deck {
    private final List<Card> drawPile;
    private final List<Card> discardPile;

    private static final int MAX_BLACK_COUNT = 4;
    private static final int MAX_VALUE = 9;

    public UnoDeck() {
        drawPile = new ArrayList<>();
        discardPile = new ArrayList<>();
        initializeDeck();
        shuffleDeck();
    }

    private void initializeDeck() {
        createStandardCards();
        createActionCards();
        createWildCards();
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

    private void createWildCards() {
        for (int i = 0; i < MAX_BLACK_COUNT; i++) {
            drawPile.add(new WildCard(WildCardType.PICK_COLOR));
            drawPile.add(new WildCard(WildCardType.PLUS_FOUR));
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
    }

    @Override
    public void shuffleDeck() {
        Collections.shuffle(drawPile);
    }

    @Override
    public Card drawCard() {
        if (drawPile.isEmpty()) {
            replenishDrawPile();
        }
        if (drawPile.isEmpty()) {
            throw new IllegalStateException("No cards left in the deck.");
        }
        return drawPile.remove(drawPile.size() - 1);
    }

    @Override
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