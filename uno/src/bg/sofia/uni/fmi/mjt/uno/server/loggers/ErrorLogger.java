package bg.sofia.uni.fmi.mjt.uno.server.loggers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class ErrorLogger {

    private static final Path LOGS_DIRECTORY = Paths.get("files", "logs");
    private static final Path DEFAULT_LOG_FILE = LOGS_DIRECTORY.resolve("server-errors.log");
    private static ErrorLogger instance;
    private final Path logFilePath;

    private ErrorLogger(Path logFilePath) {
        this.logFilePath = logFilePath;
        ensureLogDirectoryExists();
    }

    private void ensureLogDirectoryExists() {
        try {
            Files.createDirectories(LOGS_DIRECTORY);
        } catch (IOException e) {
            System.err.println("Failed to create logs directory: " + e.getMessage());
        }
    }

    public static synchronized ErrorLogger getInstance() {
        return getInstance(DEFAULT_LOG_FILE);
    }

    public static synchronized ErrorLogger getInstance(Path customLogFilePath) {
        if (instance == null) {
            instance = new ErrorLogger(customLogFilePath);
        }
        return instance;
    }

    public synchronized void log(String message, Throwable throwable, String clientInfo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath.toFile(), true));
             PrintWriter printWriter = new PrintWriter(writer)) {

            printWriter.println("===================================================");
            printWriter.println("Timestamp: " + LocalDateTime.now());
            if (clientInfo != null) {
                printWriter.println("Client Info: " + clientInfo);
            }
            printWriter.println("Log Level: ERROR");
            printWriter.println("Message: " + message);

            if (throwable != null) {
                printWriter.println("Stacktrace:");
                throwable.printStackTrace(printWriter);
            }

        } catch (IOException e) {
            System.err.println("Failed to write to error log file: " + e.getMessage());
        }
    }
}
