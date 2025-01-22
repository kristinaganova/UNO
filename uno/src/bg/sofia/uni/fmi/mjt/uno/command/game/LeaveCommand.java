package bg.sofia.uni.fmi.mjt.uno.command.game;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.util.List;

public class LeaveCommand extends PlayerCommand {

    public LeaveCommand(Player player, Game game) {
        super(player, game);
    }

    @Override
    protected String executePlayerCommand(String[] args) {
        List<Card> hand = player.getHandManager().getAllCards();
        if (!hand.isEmpty()) {
            for (Card card : hand) {
                game.getDeck().discardCard(card);
            }
        }

        game.removePlayer(player);

        return "Player " + player.getAccount().getUsername() + " has left the game.";
    }
}
