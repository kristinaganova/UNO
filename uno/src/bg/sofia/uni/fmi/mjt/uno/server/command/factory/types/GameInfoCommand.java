package bg.sofia.uni.fmi.mjt.uno.server.command.factory.types;

public enum GameInfoCommand {
    SHOW_HAND("show-hand"),
    SHOW_LAST_CARD("show-last-card"),
    SHOW_PLAYED_CARDS("show-played-cards");

    private final String command;

    GameInfoCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
