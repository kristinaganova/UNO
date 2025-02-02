package bg.sofia.uni.fmi.mjt.uno.server.command;

import bg.sofia.uni.fmi.mjt.uno.server.command.CommandValidator;
import bg.sofia.uni.fmi.mjt.uno.server.exceptions.command.CommandExecutionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandValidatorTest {

    @Test
    void testValidateArgsLengthWithCorrectLength() {
        String[] args = {"arg1", "arg2"};
        assertDoesNotThrow(() -> CommandValidator.validateArgsLength(args, 2, "Usage: <arg1> <arg2>"));
    }

    @Test
    void testExtractArgumentSuccessfully() {
        String[] args = {"--name=John", "--age=25"};
        assertEquals("John", CommandValidator.extractArgument(args, "--name=", "Usage: --name=<name> --age=<age>"));
    }

    @Test
    void testExtractArgumentThrowsExceptionWhenMissing() {
        String[] args = {"--age=25"};
        Exception exception = assertThrows(CommandExecutionException.class,
                () -> CommandValidator.extractArgument(args, "--name=", "Usage: --name=<name> --age=<age>"));

        assertTrue(exception.getMessage().contains("Missing required argument"),
                "Exception should indicate that required argument is missing.");
    }

    @Test
    void testExtractIntArgumentSuccessfully() {
        String[] args = {"--age=25"};
        assertEquals(25, CommandValidator.extractIntArgument(args, "--age=", "Usage: --age=<age>"));
    }

    @Test
    void testExtractIntArgumentThrowsExceptionForInvalidNumber() {
        String[] args = {"--age=abc"};
        Exception exception = assertThrows(CommandExecutionException.class,
                () -> CommandValidator.extractIntArgument(args, "--age=", "Usage: --age=<age>"));

        assertTrue(exception.getMessage().contains("Invalid number format"),
                "Exception should indicate invalid number format.");
    }
}
