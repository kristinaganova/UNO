package bg.sofia.uni.fmi.mjt.uno.server.loggers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class CommandLogger {
    private static final int MAX_RECENT_COMMANDS = 10;
    private final Path logFilePath;
    private final Deque<String> recentCommands;

    public CommandLogger(String gameId) {
        if (gameId == null || gameId.isBlank()) {
            throw new IllegalArgumentException("Game ID cannot be null or empty.");
        }

        this.logFilePath = Path.of("files", "logs", gameId + ".log");
        this.recentCommands = new LinkedList<>();

        try {
            Files.createDirectories(logFilePath.getParent());

            if (Files.notExists(logFilePath)) {
                Files.createFile(logFilePath);
                System.out.println("Log file created: " + logFilePath);
            } else {
                System.out.println("Log file already exists: " + logFilePath);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not initialize log file: " + logFilePath, e);
        }
    }

    public synchronized void logCommand(String command) {
        if (command == null || command.isBlank()) {
            throw new IllegalArgumentException("Command cannot be null or empty.");
        }

        try {
            Files.writeString(logFilePath, command + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("Logged command: " + command);
        } catch (IOException e) {
            throw new IllegalStateException("Could not log command: " + command, e);
        }

        synchronized (recentCommands) {
            if (recentCommands.size() == MAX_RECENT_COMMANDS) {
                recentCommands.pollFirst();
            }
            recentCommands.addLast(command);
        }
    }

    public synchronized List<String> getLoggedCommands() {
        try {
            return Files.readAllLines(logFilePath);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read log file for game.", e);
        }
    }
}
