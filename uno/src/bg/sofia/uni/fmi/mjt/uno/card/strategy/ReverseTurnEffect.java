package bg.sofia.uni.fmi.mjt.uno.card.strategy;

import bg.sofia.uni.fmi.mjt.uno.game.Game;

public class ReverseTurnEffect implements CardEffectStrategy {
    @Override
    public void applyEffect(Game game) {
        if (game.getPlayers().size() == 2) {
            game.getTurnManager().skipTurn();
        } else {
            game.getTurnManager().reverseDirection();
            game.getTurnManager().advanceTurn();
        }
    }
}
