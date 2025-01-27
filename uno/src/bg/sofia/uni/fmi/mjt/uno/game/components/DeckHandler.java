package bg.sofia.uni.fmi.mjt.uno.game.components;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.deck.UnoDeck;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

public class DeckHandler {
    private final UnoDeck deck;
    private Color currentColor;

    public DeckHandler() {
        this.deck = new UnoDeck();
        this.currentColor = null;
    }

    public Card drawCard(Player player) {
        Card card = deck.drawCard();
        player.addCardToHand(card);
        return card;
    }

    public void discardCard(Card card) {
        deck.discardCard(card);
        currentColor = card.getColor();
    }

    public Card getTopDiscardCard() {
        if (deck.getTopDiscardCard() == null) {
            Card card = deck.drawCard();
            deck.discardCard(card);
            setCurrentColor(card.getColor());
        }
        return deck.getTopDiscardCard();
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    public int getRemainingCards() {
        return deck.getRemainingCards();
    }

    public UnoDeck getDeck() {
        return deck;
    }
}
