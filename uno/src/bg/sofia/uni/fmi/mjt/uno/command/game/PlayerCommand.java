package bg.sofia.uni.fmi.mjt.uno.command.game;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.exceptions.command.CommandExecutionException;
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
            String commandString = String.join(" ", args);
            String result = executePlayerCommand(args);
            game.logCommand(commandString);

            return result;
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
        if (colorArg == null || colorArg.isBlank()) {
            throw new CommandExecutionException("Color argument cannot be null or empty.");
        }

        switch (colorArg.trim().toLowerCase()) {
            case "red":
            case "r":
                return Color.RED;
            case "blue":
            case "b":
                return Color.BLUE;
            case "green":
            case "g":
                return Color.GREEN;
            case "yellow":
            case "y":
                return Color.YELLOW;
            default:
                throw new CommandExecutionException("Invalid color. Allowed values are: red (r), blue (b), green (g), yellow (y).");
        }
    }

    protected abstract String executePlayerCommand(String[] args);

    protected void playCard(Card card, Color newColor) {
        if (card == null) {
            throw new CommandExecutionException("The card cannot be null.");
        }

        player.removeCardFromHand(card);

        game.getDeckHandler().getDeck().discardCard(card);

        game.getLogger().logCard(card);

        if (newColor != null) {
            game.getDeckHandler().setCurrentColor(newColor);
        } else {
            game.getDeckHandler().setCurrentColor(card.getColor());
        }

        card.applyEffect(game);

        game.getGameMessenger().notifyAll("Player: " + player.getAccount().getUsername()
                + " played: " + card.getCardDescription());
        game.getTurnManager().announceTurn();
    }

}
