package bg.sofia.uni.fmi.mjt.uno.server.command.factory.types;

public enum AuthCommand {
    REGISTER("register"),
    LOGIN("login"),
    LOGOUT("logout");

    private final String command;

    AuthCommand(final String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
