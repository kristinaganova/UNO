package bg.sofia.uni.fmi.mjt.uno.server.card.strategy;

import bg.sofia.uni.fmi.mjt.uno.server.game.Game;
import bg.sofia.uni.fmi.mjt.uno.server.player.Player;

public class PlusTwoEffect implements CardEffectStrategy {
    @Override
    public void applyEffect(Game game) {
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null");
        }

        Player nextPlayer = game.getTurnManager().getNextPlayer();
        for (int i = 0; i < 2; i++) {
            game.getDeckHandler().drawCard(nextPlayer);
        }
        game.getTurnManager().skipTurn();
    }
}
