package bg.sofia.uni.fmi.mjt.uno.deck;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;

public interface Deck {

    public void shuffleDeck();

    public Card drawCard();

    public void discardCard(Card card);
}