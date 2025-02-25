package bg.sofia.uni.fmi.mjt.uno.server.command.factory.types;

public enum GameActionCommand {
    PLAY_CARD("play-card"),
    PLAY_CHOOSE_COLOR("play-choose-color"),
    PLAY_PLUS_FOUR("play-plus-four"),
    DRAW_CARD("draw-card"),
    LEAVE("leave"),
    SPECTATE("spectate"),
    UNO("uno"),
    STOP_UNO("stop-uno"),
    KEEP("keep");

    private final String commandName;

    GameActionCommand(String commandName) {
        this.commandName = commandName;
    }

    public String getCommand() {
        return commandName;
    }
}
