package bg.sofia.uni.fmi.mjt.uno.server.loggers;

import bg.sofia.uni.fmi.mjt.uno.server.loggers.CommandLogger;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandLoggerTest {
    private static final String TEST_GAME_ID = "test_game";
    private static final Path LOGS_DIRECTORY = Path.of("logs");
    private Path logFilePath;
    private CommandLogger commandLogger;

    @BeforeAll
    static void setupLogsDirectory() {
        try {
            Files.createDirectories(LOGS_DIRECTORY);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create logs directory for tests.", e);
        }
    }

    @BeforeEach
    void setUp() {
        logFilePath = LOGS_DIRECTORY.resolve(TEST_GAME_ID + ".log");

        try {
            Files.deleteIfExists(logFilePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete test log file.", e);
        }

        commandLogger = new CommandLogger(TEST_GAME_ID);
    }

    @AfterEach
    void tearDown() {
        try {
            Files.deleteIfExists(logFilePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete test log file.", e);
        }
    }

    @Test
    void testLogCommandSuccess() {
        commandLogger.logCommand("Player1 played a card");

        List<String> logs = commandLogger.getLoggedCommands();

        assertFalse(logs.isEmpty(), "Log file should not be empty.");
    }

    @Test
    void testLogCommandNullOrEmptyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> commandLogger.logCommand(null));
        assertThrows(IllegalArgumentException.class, () -> commandLogger.logCommand(""));
    }
}
