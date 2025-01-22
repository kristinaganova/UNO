package bg.sofia.uni.fmi.mjt.uno.command.game;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
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
            throw new IllegalStateException("It's not your turn!");
        }
    }

    protected abstract String executePlayerCommand(String[] args);
}
