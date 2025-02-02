package bg.sofia.uni.fmi.mjt.uno.server.command.game.actions.play;

import bg.sofia.uni.fmi.mjt.uno.server.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.server.card.models.WildCard;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.WildCardType;
import bg.sofia.uni.fmi.mjt.uno.server.command.game.PlayerCommand;
import bg.sofia.uni.fmi.mjt.uno.server.game.Game;
import bg.sofia.uni.fmi.mjt.uno.server.player.Player;

public class PlayChooseColorCommand extends PlayerCommand {

    public PlayChooseColorCommand(Player player, Game game) {
        super(player, game);
    }

    @Override
    protected String executePlayerCommand(String[] args) {

        if (args == null || args.length < 2) {
            throw new IllegalArgumentException("Usage: play-choose-color " +
                    "--card-id=<card-id> --color=<red/green/blue/yellow>");
        }

        validatePlayerTurn();

        String cardId = getArgumentValue(args, "--card-id");
        String colorArg = getArgumentValue(args, "--color");
        Color chosenColor = parseColor(colorArg);

        Card cardToPlay = findCardById(cardId);

        if (!(cardToPlay instanceof WildCard) || ((WildCard) cardToPlay).getWildCardType() != WildCardType.PICK_COLOR) {
            throw new IllegalArgumentException("The specified card is not a choose color WildCard.");
        }

        playCard(cardToPlay, chosenColor);

        return "You played a choose color WildCard. The color is now " + chosenColor + ".";
    }

}
