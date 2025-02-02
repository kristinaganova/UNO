package bg.sofia.uni.fmi.mjt.uno.server.card.strategy;

import bg.sofia.uni.fmi.mjt.uno.server.game.Game;
import bg.sofia.uni.fmi.mjt.uno.server.player.Player;

public class PlusFourEffect implements CardEffectStrategy {

    private static final int CARDS_TO_DRAW = 4;
    @Override
    public void applyEffect(Game game) {
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null");
        }

        Player nextPlayer = game.getTurnManager().getNextPlayer();
        for (int i = 0; i < CARDS_TO_DRAW; i++) {
            game.getDeckHandler().drawCard(nextPlayer);
        }
        game.getTurnManager().skipTurn();
    }
}