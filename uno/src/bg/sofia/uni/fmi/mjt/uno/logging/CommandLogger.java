package bg.sofia.uni.fmi.mjt.uno.logging;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;

public class CommandLogger {
    private final Path logFilePath;

    public CommandLogger(String gameId) {
        this.logFilePath = Paths.get("logs", gameId + ".log");
        try {
            Files.createDirectories(logFilePath.getParent());
            if (!Files.exists(logFilePath)) {
                Files.createFile(logFilePath);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not create log file for game: " + gameId, e);
        }
    }

    public void logCommand(String command) {
        try (BufferedWriter writer = Files.newBufferedWriter(logFilePath, StandardOpenOption.APPEND)) {
            writer.write(LocalDateTime.now() + " - " + command);
            writer.newLine();
        } catch (IOException e) {
            throw new IllegalStateException("Could not log command: " + command, e);
        }
    }

    public List<String> getLoggedCommands() {
        try {
            return Files.readAllLines(logFilePath);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read log file for game.", e);
        }
    }
}
