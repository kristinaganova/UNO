package bg.sofia.uni.fmi.mjt.uno.command.factory;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.command.management.CreateGameCommand;
import bg.sofia.uni.fmi.mjt.uno.command.management.JoinCommand;
import bg.sofia.uni.fmi.mjt.uno.command.management.ListGamesCommand;
import bg.sofia.uni.fmi.mjt.uno.command.management.StartCommand;
import bg.sofia.uni.fmi.mjt.uno.command.management.SummaryCommand;
import bg.sofia.uni.fmi.mjt.uno.exceptions.command.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.uno.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.player.account.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameManagementCommandFactoryTest {

    private UserManager mockUserManager;
    private GameManager mockGameManager;
    private SocketChannel mockClient;
    private GameManagementCommandFactory gameManagementCommandFactory;

    @BeforeEach
    void setUp() {
        mockUserManager = mock(UserManager.class);
        mockGameManager = mock(GameManager.class);
        mockClient = mock(SocketChannel.class);

        gameManagementCommandFactory = new GameManagementCommandFactory(mockUserManager, mockGameManager);
    }

    @Test
    void testSupportsValidCommands() {
        assertTrue(gameManagementCommandFactory.supports("create-game"));
        assertTrue(gameManagementCommandFactory.supports("list-games"));
        assertTrue(gameManagementCommandFactory.supports("join"));
        assertTrue(gameManagementCommandFactory.supports("start"));
        assertTrue(gameManagementCommandFactory.supports("summary"));
    }

    @Test
    void testSupportsInvalidCommand() {
        assertFalse(gameManagementCommandFactory.supports("invalid-command"));
    }

    @Test
    void testCreateCommandCreateGame() {
        Command command = gameManagementCommandFactory.createCommand("create-game", mockClient);
        assertInstanceOf(CreateGameCommand.class, command);
    }

    @Test
    void testCreateCommandListGames() {
        Command command = gameManagementCommandFactory.createCommand("list-games", mockClient);
        assertInstanceOf(ListGamesCommand.class, command);
    }

    @Test
    void testCreateCommandJoin() {
        Command command = gameManagementCommandFactory.createCommand("join", mockClient);
        assertInstanceOf(JoinCommand.class, command);
    }

    @Test
    void testCreateCommandStart() {
        Command command = gameManagementCommandFactory.createCommand("start", mockClient);
        assertInstanceOf(StartCommand.class, command);
    }

    @Test
    void testCreateCommandSummary() {
        Command command = gameManagementCommandFactory.createCommand("summary", mockClient);
        assertInstanceOf(SummaryCommand.class, command);
    }

    @Test
    void testCreateCommandThrowsExceptionForUnknownCommand() {
        Exception exception = assertThrows(CommandNotFoundException.class,
                () -> gameManagementCommandFactory.createCommand("unknown-command", mockClient));
        assertEquals("Unknown command: unknown-command", exception.getMessage());
    }
}
