package bg.sofia.uni.fmi.mjt.uno.deck;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.card.strategy.CardEffectStrategy;
import bg.sofia.uni.fmi.mjt.uno.card.strategy.PickColorEffect;
import bg.sofia.uni.fmi.mjt.uno.card.strategy.PlusFourEffect;
import bg.sofia.uni.fmi.mjt.uno.card.strategy.PlusTwoEffect;
import bg.sofia.uni.fmi.mjt.uno.card.strategy.ReverseTurnEffect;
import bg.sofia.uni.fmi.mjt.uno.card.strategy.SkipTurnEffect;
import bg.sofia.uni.fmi.mjt.uno.card.strategy.StandardCardEffect;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.card.models.StandardCard;
import bg.sofia.uni.fmi.mjt.uno.card.models.ActionCard;
import bg.sofia.uni.fmi.mjt.uno.card.types.ActionCardType;
import bg.sofia.uni.fmi.mjt.uno.card.models.WildCard;
import bg.sofia.uni.fmi.mjt.uno.card.types.WildCardType;
import bg.sofia.uni.fmi.mjt.uno.exceptions.deck.IllegalDeckSateException;

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
        for (Color color : Color.values()) {
            if (color != Color.BLACK) {
                addStandardCards(color);
                addActionCards(color);
            }
        }
        addWildCards();
    }

    private void addStandardCards(Color color) {
        drawPile.add(new StandardCard(color, 0, new StandardCardEffect()));

        for (int i = 1; i <= MAX_VALUE; i++) {
            drawPile.add(new StandardCard(color, i, new StandardCardEffect()));
            drawPile.add(new StandardCard(color, i, new StandardCardEffect()));
        }
    }

    private void addActionCards(Color color) {
        for (ActionCardType type : ActionCardType.values()) {
            drawPile.add(new ActionCard(color, type, getEffectForActionCard(type)));
            drawPile.add(new ActionCard(color, type, getEffectForActionCard(type)));
        }
    }

    private void addWildCards() {
        for (int i = 0; i < MAX_BLACK_COUNT; i++) {
            drawPile.add(new WildCard(WildCardType.PICK_COLOR, new PickColorEffect()));
            drawPile.add(new WildCard(WildCardType.PLUS_FOUR, new PlusFourEffect()));
        }
    }

    private CardEffectStrategy getEffectForActionCard(ActionCardType type) {
        return switch (type) {
            case SKIP -> new SkipTurnEffect();
            case REVERSE -> new ReverseTurnEffect();
            case PLUS_TWO -> new PlusTwoEffect();
            default -> throw new IllegalArgumentException("Unknown action card type: " + type);
        };
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
            throw new IllegalDeckSateException("No cards left in the deck. Game cannot continue.");
        }
        return drawPile.remove(drawPile.size() - 1);
    }

    @Override
    public void discardCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null.");
        }
        discardPile.add(card);
    }

    protected void replenishDrawPile() {
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
