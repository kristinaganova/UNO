package bg.sofia.uni.fmi.mjt.uno.card.strategy;

import bg.sofia.uni.fmi.mjt.uno.game.Game;

public class StandardCardEffect implements CardEffectStrategy {
    @Override
    public void applyEffect(Game game) {
        game.getTurnManager().advanceTurn();
    }
}
