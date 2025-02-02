package bg.sofia.uni.fmi.mjt.uno.server.deck;

import bg.sofia.uni.fmi.mjt.uno.server.card.models.Card;

public interface Deck {

    public void shuffleDeck();

    public Card drawCard();

    public void discardCard(Card card);
}