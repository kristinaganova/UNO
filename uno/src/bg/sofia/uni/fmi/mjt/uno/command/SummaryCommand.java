package bg.sofia.uni.fmi.mjt.uno.command;

import bg.sofia.uni.fmi.mjt.uno.game.GameManager;

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

        return gameManager.getGameSummary(gameId);
    }
}
