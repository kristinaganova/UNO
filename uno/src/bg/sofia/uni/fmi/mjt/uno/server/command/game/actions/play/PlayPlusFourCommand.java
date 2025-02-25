package bg.sofia.uni.fmi.mjt.uno.server.command.game.actions.play;

import bg.sofia.uni.fmi.mjt.uno.server.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.server.card.models.WildCard;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.WildCardType;
import bg.sofia.uni.fmi.mjt.uno.server.command.game.PlayerCommand;
import bg.sofia.uni.fmi.mjt.uno.server.exceptions.command.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.Game;
import bg.sofia.uni.fmi.mjt.uno.server.player.Player;

public class PlayPlusFourCommand extends PlayerCommand {

    public PlayPlusFourCommand(Player player, Game game) {
        super(player, game);
    }

    @Override
    protected String executePlayerCommand(String[] args) {
        validatePlayerTurn();

        if (args == null || args.length < 2) {
            throw new CommandExecutionException("Usage: play-plus-four " +
                    "--card-id=<card-id> --color=<red/green/blue/yellow>");
        }

        String cardId = getArgumentValue(args, "--card-id");
        String colorArg = getArgumentValue(args, "--color");
        Color chosenColor = parseColor(colorArg);

        Card cardToPlay = findCardById(cardId);

        if (!(cardToPlay instanceof WildCard) || ((WildCard) cardToPlay).getWildCardType() != WildCardType.PLUS_FOUR) {
            throw new IllegalArgumentException("The specified card is not a +4 WildCard.");
        }

        playCard(cardToPlay, chosenColor);

        return "You played a +4 WildCard. The color is now " + chosenColor + ".";
    }
}
