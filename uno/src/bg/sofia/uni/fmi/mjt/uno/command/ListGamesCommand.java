package bg.sofia.uni.fmi.mjt.uno.command;

import bg.sofia.uni.fmi.mjt.uno.exceptions.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.game.GameManager;

public class ListGamesCommand extends AbstractCommand {
    private final GameManager gameManager;

    public ListGamesCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    protected String executeCommand(String[] args) {
        String status = args.length > 0 ? args[0].toLowerCase() : "all";

        switch (status) {
            case "started":
                return gameManager.getGamesByStatus("started");
            case "ended":
                return gameManager.getGamesByStatus("ended");
            case "available":
                return gameManager.getGamesByStatus("available");
            case "all":
                return gameManager.getGamesByStatus("all");
            default:
                throw new CommandExecutionException("Unknown status: " + status);
        }
    }
}