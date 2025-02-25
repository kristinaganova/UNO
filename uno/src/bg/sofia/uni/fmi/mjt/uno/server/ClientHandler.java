package bg.sofia.uni.fmi.mjt.uno.server;

import bg.sofia.uni.fmi.mjt.uno.server.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.uno.server.games.game.Game;
import bg.sofia.uni.fmi.mjt.uno.server.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.server.loggers.ErrorLogger;
import bg.sofia.uni.fmi.mjt.uno.server.player.account.UserManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientHandler implements Runnable {

    private static final int BUFFER_CAPACITY = 1024;
    private final SocketChannel client;
    private final CommandExecutor commandExecutor;
    private final ErrorLogger errorLogger;

    public ClientHandler(SocketChannel client, CommandExecutor commandExecutor) {
        this.client = client;
        this.commandExecutor = commandExecutor;
        errorLogger = ErrorLogger.getInstance();
    }

    @Override
    public void run() {
        try {
            String message = readMessageFromClient();
            if (message == null || message.isEmpty()) {
                return;
            }

            System.out.println("Received: " + message + " from " + client.getRemoteAddress());

            String[] parts = parseCommand(message);
            String commandName = parts[0];
            String[] args = parts[1].isEmpty() ? new String[0] : parts[1].split(" ");

            System.out.println("Command: " + commandName + ", Args: " + String.join(", ", args));

            String response = commandExecutor.executeCommand(commandName, args, client);
            if (response != null) {
                sendMessageToClient(response);
            }
        } catch (Exception e) {
            logError(e);
            disconnectClient();
        }
    }

    private String readMessageFromClient() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CAPACITY);
        int bytesRead = client.read(buffer);

        if (bytesRead == -1) {
            disconnectClient();
            return null;
        }

        buffer.flip();
        return new String(buffer.array(), 0, buffer.limit()).trim();
    }

    private String[] parseCommand(String message) {
        String[] parts = message.split(" ", 2);
        String commandName = parts[0];
        String commandArgs = parts.length > 1 ? parts[1] : "";
        return new String[]{commandName, commandArgs};
    }

    private void sendMessageToClient(String message) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap((message + System.lineSeparator()).getBytes());
            client.write(buffer);
        } catch (IOException e) {
            logError(e);
            disconnectClient();
        }
    }

    private void disconnectClient() {
        try {
            UserManager userManager = UserManager.getInstance();
            String username = userManager.getLoggedInUsername(client);
            if (username != null) {
                System.out.println("Client disconnected: " + username);
                userManager.logout(client);

                GameManager gameManager = GameManager.getInstance();
                Game currentGame = gameManager.getGameByPlayer(username);
                if (currentGame != null) {
                    currentGame.disconnectPlayer(username);
                }
            }
            client.close();
        } catch (IOException e) {
            logError(e);
        }
    }

    private void logError(Exception e) {
        String clientInfo = getClientInfo();
        errorLogger.log("Error occurred while handling client: " + clientInfo, e, clientInfo);
    }

    private String getClientInfo() {
        try {
            UserManager userManager = UserManager.getInstance();
            String username = userManager.getLoggedInUsername(client);
            String clientAddress = client.getRemoteAddress().toString();
            return "Username: " + (username != null ? username : "Unknown") + ", Address: " + clientAddress;
        } catch (IOException e) {
            return "Unknown (error retrieving client info)";
        }
    }
}
