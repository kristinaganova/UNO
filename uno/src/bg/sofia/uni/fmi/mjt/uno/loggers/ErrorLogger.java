package bg.sofia.uni.fmi.mjt.uno.loggers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class ErrorLogger {

    private static final Path LOGS_DIRECTORY = Paths.get("logs");
    private static final String DEFAULT_LOG_FILE = LOGS_DIRECTORY.resolve("server-errors.log").toString();
    private static ErrorLogger instance;
    private final String logFile;

    private ErrorLogger(String logFile) {
        this.logFile = logFile;
        ensureLogDirectoryExists(); // Ensure logs/ exists before writing
    }

    private void ensureLogDirectoryExists() {
        try {
            Files.createDirectories(LOGS_DIRECTORY);
        } catch (IOException e) {
            System.err.println("Failed to create logs directory: " + e.getMessage());
        }
    }

    public static synchronized ErrorLogger getInstance() {
        if (instance == null) {
            instance = new ErrorLogger(DEFAULT_LOG_FILE);
        }
        return instance;
    }

    public static synchronized ErrorLogger getInstance(String customLogFile) {
        if (instance == null) {
            instance = new ErrorLogger(customLogFile);
        }
        return instance;
    }

    public void log(String message, Throwable throwable, String clientInfo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
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
