package bg.sofia.uni.fmi.mjt.uno.command;

import bg.sofia.uni.fmi.mjt.uno.command.factory.CommandFactory;
import bg.sofia.uni.fmi.mjt.uno.exceptions.command.CommandExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandExecutorTest {
    private CommandExecutor commandExecutor;
    private CommandFactory commandFactoryMock;
    private Command commandMock;
    private SocketChannel clientMock;

    @BeforeEach
    void setUp() {
        commandFactoryMock = mock(CommandFactory.class);
        commandMock = mock(Command.class);
        clientMock = mock(SocketChannel.class);
        commandExecutor = new CommandExecutor(commandFactoryMock);
    }

    @Test
    void testExecuteCommandSuccessfully() throws CommandExecutionException {
        when(commandFactoryMock.createCommand("test", clientMock)).thenReturn(commandMock);
        when(commandMock.execute(null)).thenReturn("Command executed successfully");

        String result = commandExecutor.executeCommand("test", null, clientMock);
        assertEquals("Command executed successfully", result, "Command should execute successfully.");
    }

    @Test
    void testExecuteCommandThrowsCommandExecutionException() throws CommandExecutionException {
        when(commandFactoryMock.createCommand("error", clientMock)).thenThrow(new CommandExecutionException("Execution failed"));

        String result = commandExecutor.executeCommand("error", null, clientMock);
        assertEquals("Command execution error: Execution failed", result, "Should return a meaningful error message.");
    }

    @Test
    void testExecuteCommandThrowsIllegalArgumentException() throws CommandExecutionException {
        when(commandFactoryMock.createCommand("invalid", clientMock)).thenThrow(new IllegalArgumentException("Invalid command"));

        String result = commandExecutor.executeCommand("invalid", null, clientMock);
        assertEquals("Invalid command: Invalid command", result, "Should return a meaningful error message for invalid command.");
    }

    @Test
    void testExecuteCommandThrowsUnexpectedException() throws CommandExecutionException {
        when(commandFactoryMock.createCommand("unexpected", clientMock)).thenThrow(new RuntimeException("Unexpected error"));

        String result = commandExecutor.executeCommand("unexpected", null, clientMock);
        assertTrue(result.startsWith("Unexpected error occurred:"), "Should return a general unexpected error message.");
    }
}
