package bg.sofia.uni.fmi.mjt.uno.command.game.play;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.card.models.WildCard;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.card.types.WildCardType;
import bg.sofia.uni.fmi.mjt.uno.command.game.PlayerCommand;
import bg.sofia.uni.fmi.mjt.uno.exceptions.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

public class PlayPlusFourCommand extends PlayerCommand {

    public PlayPlusFourCommand(Player player, Game game) {
        super(player, game);
    }

    @Override
    protected String executePlayerCommand(String[] args) {
        if (args == null || args.length < 2) {
            throw new CommandExecutionException("Usage:" +
                    " play-plus-four --card-id=<card-id> --color=<red/green/blue/yellow>");
        }

        String cardId = getArgumentValue(args, "--card-id");
        String colorArg = getArgumentValue(args, "--color");
        Color chosenColor = parseColor(colorArg);

        Card cardToPlay = findCardById(cardId);

        if (!(cardToPlay instanceof WildCard) || ((WildCard) cardToPlay).getWildCardType() != WildCardType.PLUS_FOUR) {
            throw new IllegalArgumentException("The specified card is not a +4 WildCard.");
        }

        player.removeCardFromHand(cardToPlay);
        game.getDeck().discardCard(cardToPlay);
        game.setCurrentColor(chosenColor);

        ((WildCard) cardToPlay).applyEffect(game);

        return "You played a +4 WildCard. The color is now " + chosenColor + ".";
    }

}
