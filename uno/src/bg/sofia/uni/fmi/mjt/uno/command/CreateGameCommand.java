package bg.sofia.uni.fmi.mjt.uno.command;

import bg.sofia.uni.fmi.mjt.uno.game.GameManager;

public class CreateGameCommand extends AbstractCommand {
    private final GameManager gameManager;

    private static final String USAGE = "create-game --number-of-players=<number> --game-id=<game-id>";

    public CreateGameCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    protected String executeCommand(String[] args) {
        CommandValidator.validateArgsLength(args, 2, USAGE);

        int numberOfPlayers = Integer.parseInt(args[0]);
        String gameId = args[1];

        if (gameManager.createGame(gameId, numberOfPlayers)) {
            return "Game created successfully with ID: " + gameId;
        } else {
            return "Game creation failed. Game ID may already exist.";
        }
    }
}

