package bg.sofia.uni.fmi.mjt.uno.server.card.strategy;

import bg.sofia.uni.fmi.mjt.uno.server.games.game.Game;

public class ReverseTurnEffect implements CardEffectStrategy {
    @Override
    public void applyEffect(Game game) {
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null");
        }

        if (game.getPlayerRegistry().getPlayers().size() == 2) {
            game.getTurnManager().skipTurn();
        } else {
            game.getTurnManager().reverseDirection();
            game.advanceTurn();
        }
    }
}
