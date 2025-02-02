package bg.sofia.uni.fmi.mjt.uno.server.command.game;

import bg.sofia.uni.fmi.mjt.uno.server.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.server.card.models.StandardCard;
import bg.sofia.uni.fmi.mjt.uno.server.card.strategy.StandardCardEffect;
import bg.sofia.uni.fmi.mjt.uno.server.card.types.Color;
import bg.sofia.uni.fmi.mjt.uno.server.command.game.PlayerCommand;
import bg.sofia.uni.fmi.mjt.uno.server.exceptions.command.CommandExecutionException;
import bg.sofia.uni.fmi.mjt.uno.server.game.Game;

import bg.sofia.uni.fmi.mjt.uno.server.player.Hand;
import bg.sofia.uni.fmi.mjt.uno.server.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerCommandTest {
    private Player playerMock;
    private Game gameMock;
    private Hand handMock;
    private Card testCard;
    private PlayerCommand command;

    @BeforeEach
    void setUp() {
        playerMock = mock(Player.class);
        gameMock = mock(Game.class);
        handMock = mock(Hand.class);

        testCard = new StandardCard(Color.RED, 5, new StandardCardEffect());

        when(playerMock.getHandManager()).thenReturn(handMock);
        when(handMock.getAllCards()).thenReturn(Collections.singletonList(testCard));

        command = new TestPlayerCommand(playerMock, gameMock);
    }

    @Test
    void testExecuteCommandSuccessfully() {
        String result = command.execute("commandName", new String[]{"some", "args"});
        assertEquals("Test executed", result, "Expected successful execution message.");
    }

    @Test
    void testExecuteCommandHandlesException() {
        PlayerCommand failingCommand = new TestPlayerCommand(playerMock, gameMock) {
            @Override
            protected String executePlayerCommand(String[] args) {
                throw new RuntimeException("Test failure");
            }
        };

        String result = failingCommand.execute("commandName", new String[]{"args"});
        assertTrue(result.contains("Error: Test failure"), "Should return an error message.");
    }

    @Test
    void testValidatePlayerTurnThrowsExceptionWhenNotPlayerTurn() {
        when(gameMock.getTurnManager()).thenThrow(new CommandExecutionException("It's not your turn!"));

        Exception exception = assertThrows(CommandExecutionException.class, command::validatePlayerTurn);
        assertEquals("It's not your turn!", exception.getMessage(), "Should throw an exception if it's not the player's turn.");
    }

    @Test
    void testFindCardByIdThrowsExceptionWhenCardNotFound() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> command.findCardById("999"));
        assertTrue(exception.getMessage().contains("Card with ID 999 does not exist"), "Should throw an exception if the card is missing.");
    }

    @Test
    void testParseColorSuccessfully() {
        assertEquals(Color.RED, command.parseColor("red"));
        assertEquals(Color.BLUE, command.parseColor("b"));
        assertEquals(Color.GREEN, command.parseColor("Green"));
        assertEquals(Color.YELLOW, command.parseColor("Y"), "Should be case insensitive and support shortcuts.");
    }

    @Test
    void testParseColorThrowsExceptionOnInvalidColor() {
        Exception exception = assertThrows(CommandExecutionException.class, () -> command.parseColor("purple"));
        assertTrue(exception.getMessage().contains("Invalid color"), "Should throw an exception for invalid color.");
    }

    private static class TestPlayerCommand extends PlayerCommand {
        public TestPlayerCommand(Player player, Game game) {
            super(player, game);
        }

        @Override
        protected String executePlayerCommand(String[] args) {
            return "Test executed";
        }
    }
}
