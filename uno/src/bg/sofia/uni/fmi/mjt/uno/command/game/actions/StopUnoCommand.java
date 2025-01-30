package bg.sofia.uni.fmi.mjt.uno.command.game.actions;

import bg.sofia.uni.fmi.mjt.uno.command.game.PlayerCommand;
import bg.sofia.uni.fmi.mjt.uno.exceptions.command.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

public class StopUnoCommand extends PlayerCommand {

    public StopUnoCommand(Player player, Game game) {
        super(player, game);
    }

    @Override
    protected String executePlayerCommand(String[] args) throws CommandExecutionException {
        if (args.length < 1) {
            throw new CommandExecutionException("Usage: stop-uno --target=<player-name>");
        }

        String targetUsername = getArgumentValue(args, "--target");
        Player targetPlayer = game.getPlayerRegistry().getPlayers().stream()
                .filter(p -> p.getAccount().getUsername().equals(targetUsername))
                .findFirst()
                .orElse(null);

        if (targetPlayer == null) {
            throw new CommandExecutionException("Player " + targetUsername + " not found in the game.");
        }

        if (targetPlayer.getHandSize() != 1) {
            throw new CommandExecutionException("You can only call STOP UNO on a player with exactly one card.");
        }

        if (targetPlayer.hasCalledUno()) {
            return targetUsername + " has already declared UNO, no penalty applied.";
        }

        game.getDeckHandler().drawCard(targetPlayer);
        game.getDeckHandler().drawCard(targetPlayer);

        game.getGameMessenger().notifyAll(targetUsername + " was caught not saying uno and drew two cards.");

        return targetUsername + " was caught and drew two cards.";
    }
}
