package bg.sofia.uni.fmi.mjt.uno.server.loggers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorLoggerTest {

    private static final Path LOGS_DIRECTORY = Paths.get("logs");
    private static final Path LOG_FILE_PATH = LOGS_DIRECTORY.resolve("test-server-errors.log");

    private ErrorLogger logger;

    @BeforeEach
    void setUp() {
        logger = ErrorLogger.getInstance(LOG_FILE_PATH);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(LOG_FILE_PATH);
    }

    @Test
    void testSingletonInstance() {
        ErrorLogger logger2 = ErrorLogger.getInstance();
        assertSame(logger, logger2, "ErrorLogger should follow Singleton pattern.");
    }

    @Test
    void testLogFileCreation() {
        logger.log("Test message", null, "Client1");

        assertTrue(Files.exists(LOG_FILE_PATH), "Log file should be created if it does not exist.");
    }

    @Test
    void testLogErrorMessage() throws IOException {
        String testMessage = "Test error occurred";
        logger.log(testMessage, null, null);

        List<String> lines = Files.readAllLines(LOG_FILE_PATH);
        boolean containsMessage = lines.stream().anyMatch(line -> line.contains(testMessage));

        assertTrue(containsMessage, "Log file should contain the logged message.");
    }

    @Test
    void testLogExceptionStacktrace() throws IOException {
        Exception testException = new RuntimeException("Test exception");
        logger.log("Exception test", testException, null);

        List<String> lines = Files.readAllLines(LOG_FILE_PATH);
        boolean containsStackTrace = lines.stream().anyMatch(line -> line.contains("RuntimeException"));

        assertTrue(containsStackTrace, "Log file should contain the stack trace of the exception.");
    }

    @Test
    void testLogWithClientInfo() throws IOException {
        String clientInfo = "Client IP: 192.168.1.1";
        logger.log("Client error test", null, clientInfo);

        List<String> lines = Files.readAllLines(LOG_FILE_PATH);
        boolean containsClientInfo = lines.stream().anyMatch(line -> line.contains(clientInfo));

        assertTrue(containsClientInfo, "Log file should contain client information.");
    }
}
