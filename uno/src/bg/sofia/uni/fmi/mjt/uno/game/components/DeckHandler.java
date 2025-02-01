package bg.sofia.uni.fmi.mjt.uno.game.components;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.card.types.CardType;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.deck.UnoDeck;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

public class DeckHandler {

    private final UnoDeck deck;
    private Color currentColor;

    public DeckHandler() {
        this.deck = new UnoDeck();
        this.currentColor = getTopDiscardCard().getColor();
    }

    public Card drawCard(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        Card card = deck.drawCard();
        player.getHand().setLastDrawnCard(card);
        player.sendMessage("You drew: " + card.getCardDescription());
        return card;
    }

    public void discardCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Cannot discard a null card.");
        }
        deck.discardCard(card);
        currentColor = card.getColor();
    }

    public Card getTopDiscardCard() {
        if (deck.getTopDiscardCard() == null) {
            Card card;
            int maxAttempts = deck.getRemainingCards();

            do {
                if (maxAttempts-- == 0) {
                    throw new IllegalStateException("No non-wild cards available to start the discard pile.");
                }
                card = deck.drawCard();
                deck.discardCard(card);
            } while (card.getCardType() == CardType.WILD);

            setCurrentColor(card.getColor());
        }
        return deck.getTopDiscardCard();
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(Color color) {
        if (color == null) {
            throw new IllegalArgumentException("color cannot be null");
        }
        this.currentColor = color;
    }

    public int getRemainingCards() {
        return deck.getRemainingCards();
    }

    public UnoDeck getDeck() {
        return deck;
    }
}
