package bg.sofia.uni.fmi.mjt.uno.command.game;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.command.game.play.PlayCardCommand;
import bg.sofia.uni.fmi.mjt.uno.exceptions.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.util.List;

public class ShowPlayedCardsCommand extends PlayCardCommand {

    public ShowPlayedCardsCommand(Player player, Game game) {
        super(player, game);
    }

    @Override
    public String execute(String[] args) {
        List<Card> playedCardsList = player.getCardLogger().getPlayedCards();

        if (playedCardsList.isEmpty()) {
            throw new CommandExecutionException("No played cards found");
        }

        StringBuilder playedCards = new StringBuilder("Played cards:\n");
        for (Card card : playedCardsList) {
            playedCards.append(card.getCardDescription()).append("\n");
        }
        return playedCards.toString();
    }
}
