package bg.sofia.uni.fmi.mjt.uno.card.strategy;

import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

public class PlusFourEffect implements CardEffectStrategy {

    private static final int CARDS_TO_DRAW = 4;
    @Override
    public void applyEffect(Game game) {
        Player nextPlayer = game.getTurnManager().getNextPlayer();
        for (int i = 0; i < CARDS_TO_DRAW; i++) {
            game.drawCard(nextPlayer);
        }
        game.getTurnManager().skipTurn();
    }
}