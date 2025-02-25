package bg.sofia.uni.fmi.mjt.uno.server.command.factory.types;

public enum GameManagementCommand {
    CREATE("create-game"),
    LIST("list-games"),
    JOIN("join"),
    START("start"),
    SUMMARY("summary");

    private final String command;

    GameManagementCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
