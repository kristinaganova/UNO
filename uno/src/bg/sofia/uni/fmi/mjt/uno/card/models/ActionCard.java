package bg.sofia.uni.fmi.mjt.uno.card.models;

import bg.sofia.uni.fmi.mjt.uno.card.types.ActionCardType;
import bg.sofia.uni.fmi.mjt.uno.card.types.CardType;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.game.TurnManager;
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
    public boolean isPlayable(Card other, Color currentColor) {
        return other.isPlayableWithAction(this, currentColor);
    }

    @Override
    public boolean isPlayableWithStandard(StandardCard other, Color currentColor) {
        return this.getColor() == other.getColor();
    }

    @Override
    public boolean isPlayableWithAction(ActionCard other, Color currentColor) {
        return this.getColor() == other.getColor() || this.type == other.type;
    }

    @Override
    public boolean isPlayableWithWild(WildCard other, Color currentColor) {
        return this.getColor() == currentColor;
    }

    @Override
    public void applyEffect(Game game) {
        TurnManager turnManager = game.getTurnManager();

        switch (type) {
            case SKIP:
                turnManager.skipTurn();
                break;
            case REVERSE:
                turnManager.reverseDirection();
                break;
            case PLUS_TWO:
                Player nextPlayer = turnManager.getNextPlayer();
                for (int i = 0; i < 2; i++) {
                    game.drawCard(nextPlayer);
                }
                turnManager.advanceTurn();
                break;
            default:
                throw new IllegalArgumentException("Unknown action card type: " + type);
        }
    }

}
