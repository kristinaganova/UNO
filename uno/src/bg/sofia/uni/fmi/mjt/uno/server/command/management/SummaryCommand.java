package bg.sofia.uni.fmi.mjt.uno.server.command.management;

import bg.sofia.uni.fmi.mjt.uno.server.command.AbstractCommand;
import bg.sofia.uni.fmi.mjt.uno.server.command.CommandValidator;
import bg.sofia.uni.fmi.mjt.uno.server.games.GameManager;

public class SummaryCommand extends AbstractCommand {
    private static final String USAGE = "summary --game-id=<game-id>";
    private final GameManager gameManager;

    public SummaryCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    protected String executeCommand(String[] args) {
        CommandValidator.validateArgsLength(args, 1, USAGE);
        String gameId = CommandValidator.extractArgument(args, "--game-id=", USAGE);

        return gameManager.getGame(gameId).getSummary();
    }
}
