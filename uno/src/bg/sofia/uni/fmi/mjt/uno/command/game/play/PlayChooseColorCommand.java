package bg.sofia.uni.fmi.mjt.uno.command.game.play;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.card.models.WildCard;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.card.types.WildCardType;
import bg.sofia.uni.fmi.mjt.uno.command.game.PlayerCommand;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

public class PlayChooseColorCommand extends PlayerCommand {

    public PlayChooseColorCommand(Player player, Game game) {
        super(player, game);
    }

    @Override
    protected String executePlayerCommand(String[] args) {
        if (args == null || args.length < 2) {
            throw new IllegalArgumentException("Usage: play-choose-color --card-id=<card-id> --color=<red/green/blue/yellow>");
        }

        String cardId = getArgumentValue(args, "--card-id");
        String colorArg = getArgumentValue(args, "--color");
        Color chosenColor = parseColor(colorArg);

        Card cardToPlay = findCardById(cardId);

        if (!(cardToPlay instanceof WildCard) || ((WildCard) cardToPlay).getWildCardType() != WildCardType.PICK_COLOR) {
            throw new IllegalArgumentException("The specified card is not a choose color WildCard.");
        }

        player.removeCardFromHand(cardToPlay);
        game.getDeck().discardCard(cardToPlay);

        game.setCurrentColor(chosenColor);

        return "You played a choose color WildCard. The color is now " + chosenColor + ".";
    }

    private String getArgumentValue(String[] args, String key) {
        for (String arg : args) {
            if (arg.startsWith(key + "=")) {
                return arg.substring((key + "=").length());
            }
        }
        throw new IllegalArgumentException("Missing argument: " + key);
    }

    private Color parseColor(String colorArg) {
        try {
            return Color.valueOf(colorArg.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid color. Allowed values are: red, green, blue, yellow.");
        }
    }

    private Card findCardById(String cardId) {
        for (Card card : player.getHandManager().getAllCards()) {
            if (card.getId().equals(cardId)) {
                return card;
            }
        }
        throw new IllegalArgumentException("Card with ID " + cardId + " does not exist in your hand.");
    }
}
