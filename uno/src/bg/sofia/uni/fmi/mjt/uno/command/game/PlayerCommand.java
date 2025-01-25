package bg.sofia.uni.fmi.mjt.uno.command.game;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.exceptions.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

public abstract class PlayerCommand implements Command {
    protected final Player player;
    protected final Game game;

    public PlayerCommand(Player player, Game game) {
        if (player == null || game == null) {
            throw new IllegalArgumentException("Player and game cannot be null.");
        }
        this.player = player;
        this.game = game;
    }

    @Override
    public String execute(String[] args) {
        try {
            validatePlayerTurn();
            return executePlayerCommand(args);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    protected void validatePlayerTurn() {
        if (!game.getTurnManager().getCurrentPlayer().equals(player)) {
            throw new CommandExecutionException("It's not your turn!");
        }
    }

    protected String getArgumentValue(String[] args, String key) {
        for (String arg : args) {
            if (arg.startsWith(key + "=")) {
                return arg.substring((key + "=").length());
            }
        }
        throw new CommandExecutionException("Missing argument: " + key);
    }

    protected Card findCardById(String cardId) {
        return player.getHandManager()
                .getAllCards()
                .stream()
                .filter(card -> card.getId().toString().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Card with ID " + cardId
                        + " does not exist in your hand."));
    }

    protected Color parseColor(String colorArg) {
        try {
            return Color.valueOf(colorArg.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommandExecutionException("Invalid color. Allowed values are: red, green, blue, yellow.");
        }
    }

    protected abstract String executePlayerCommand(String[] args);
}
