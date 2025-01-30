package bg.sofia.uni.fmi.mjt.uno.command.game.actions;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.command.game.PlayerCommand;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

public class DrawCardCommand extends PlayerCommand {

    public DrawCardCommand(Player player, Game game) {
        super(player, game);
    }

    @Override
    protected String executePlayerCommand(String[] args) {
        validatePlayerTurn();

        Card drawnCard = game.getDeckHandler().drawCard(player);
        game.getTurnManager().advanceTurn();
        game.getGameMessenger().notifyAll("user: " + player.getAccount().getUsername() +
                " draw card." + System.lineSeparator());
        game.getTurnManager().announceTurn();
        return "You drew: " + drawnCard.getCardDescription();
    }
}