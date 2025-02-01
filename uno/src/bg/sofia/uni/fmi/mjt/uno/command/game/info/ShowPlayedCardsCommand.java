package bg.sofia.uni.fmi.mjt.uno.command.game.info;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.command.game.actions.play.PlayCardCommand;
import bg.sofia.uni.fmi.mjt.uno.exceptions.command.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.util.List;

public class ShowPlayedCardsCommand extends PlayCardCommand {

    public ShowPlayedCardsCommand(Player player, Game game) {
        super(player, game);
    }

    @Override
    public String execute(String commandName, String[] args) {
        List<Card> playedCardsList = game.getLogger().getPlayedCards();

        if (playedCardsList.isEmpty()) {
            throw new CommandExecutionException("No played cards found.");
        }

        return game.getLogger().getLogSummary();
    }

}
