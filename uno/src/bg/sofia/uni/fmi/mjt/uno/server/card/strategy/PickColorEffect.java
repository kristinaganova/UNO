package bg.sofia.uni.fmi.mjt.uno.server.card.strategy;

import bg.sofia.uni.fmi.mjt.uno.server.game.Game;

public class PickColorEffect implements CardEffectStrategy {
    @Override
    public void applyEffect(Game game) {
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null");
        }
        game.advanceTurn();
    }
}