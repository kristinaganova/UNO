package bg.sofia.uni.fmi.mjt.uno.command;

import bg.sofia.uni.fmi.mjt.uno.exceptions.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.game.GameManager;

public class ListGamesCommand extends AbstractCommand {
    private final GameManager gameManager;

    private static final String USAGE = "list-games --status=<started/ended/available/all>";

    public ListGamesCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    protected String executeCommand(String[] args) {
        String status = parseStatusArgument(args);

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
                throw new CommandExecutionException("Unknown status: " + status + ". " + USAGE);
        }
    }

    private String parseStatusArgument(String[] args) {
        if (args.length == 0) {
            return "all";
        }

        String statusArg = args[0];

        if (!statusArg.startsWith("--status=")) {
            throw new CommandExecutionException("Invalid argument format. " + USAGE);
        }

        String status = statusArg.substring("--status=".length()).toLowerCase();

        if (!isValidStatus(status)) {
            throw new CommandExecutionException("Invalid status: " + status + ". " + USAGE);
        }

        return status;
    }

    private boolean isValidStatus(String status) {
        return status.equals("started") || status.equals("ended") || status.equals("available") || status.equals("all");
    }
}