package bg.sofia.uni.fmi.mjt.uno.command;

import bg.sofia.uni.fmi.mjt.uno.exceptions.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.game.GameManager;

public class StartCommand extends AbstractCommand {
    private static final String USAGE = "start --game-id=<game-id>";
    private final GameManager gameManager;

    public StartCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    protected String executeCommand(String[] args) {
        CommandValidator.validateArgsLength(args, 1, USAGE);
        String gameId = CommandValidator.extractArgument(args, "--game-id=", USAGE);

        boolean started = gameManager.startGame(gameId);
        if (started) {
            return "Game " + gameId + " started successfully.";
        } else {
            throw new CommandExecutionException("Game " + gameId + " could not be started.");
        }
    }
}
