package bg.sofia.uni.fmi.mjt.uno.player;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.logging.CardLogger;
import bg.sofia.uni.fmi.mjt.uno.player.account.Account;

public class Player {
    private final Account account;
    private final Hand handManager;
    private final CardLogger cardLogger;
    private boolean unoCalled;
    private Game currentGame;

    public Player(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null.");
        }
        this.account = account;
        this.handManager = new Hand();
        this.cardLogger = new CardLogger();
        this.unoCalled = false;
    }

    public CardLogger getCardLogger() {
        return cardLogger;
    }

    public Account getAccount() {
        return account;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public void setGame(Game game) {
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null.");
        }
        this.currentGame = game;
    }

    public void addCardToHand(Card card) {
        handManager.addCard(card);
        unoCalled = false;
    }

    public boolean removeCardFromHand(Card card) {
        boolean removed = handManager.removeCard(card);
        if (getHandSize() == 1 && !unoCalled) {
            System.out.println(account.getUsername() + " forgot to call UNO!");
        }
        return removed;
    }

    public int getHandSize() {
        return handManager.getSize();
    }

    public void callUno() {
        if (getHandSize() == 1) {
            unoCalled = true;
        } else {
            throw new IllegalStateException("UNO can only be called with one card left.");
        }
    }

    public boolean hasCalledUno() {
        return unoCalled;
    }

    public String showHand() {
        return handManager.showHand();
    }

    public Hand getHandManager() {
        return handManager;
    }
}