package bg.sofia.uni.fmi.mjt.uno.card.models;

import bg.sofia.uni.fmi.mjt.uno.card.types.ActionCardType;
import bg.sofia.uni.fmi.mjt.uno.card.types.CardType;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

public non-sealed class ActionCard extends Card {
    private final ActionCardType type;

    public ActionCard(ActionCardType type, Color color) {
        super(color, CardType.ACTION);
        if (color == Color.BLACK) {
            throw new IllegalArgumentException("Black cards can only be Wild Cards(Pick Color or Plus 4.)");
        }
        this.type = type;
    }

    public ActionCardType getType() {
        return type;
    }

    @Override
    public String getCardDescription() {
        return type.getDescription();
    }

    @Override
    public boolean isPlayable(Card topCard) {
        return this.getColor() == topCard.getColor() ||
                (topCard instanceof ActionCard && this.type == ((ActionCard) topCard).type);
    }

    @Override
    public void applyEffect(Game game) {
        switch (type) {
            case SKIP -> game.getTurnManager().skipTurn();
            case REVERSE -> game.getTurnManager().reverseDirection();
            case PLUS_TWO -> {
                Player nextPlayer = game.getTurnManager().getNextPlayer();
                game.drawCard(nextPlayer);
                game.drawCard(nextPlayer);
                game.getTurnManager().skipTurn();
            }
        }
    }
}
