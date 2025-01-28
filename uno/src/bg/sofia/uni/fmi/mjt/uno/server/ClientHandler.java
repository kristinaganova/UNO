package bg.sofia.uni.fmi.mjt.uno.server;

import bg.sofia.uni.fmi.mjt.uno.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.uno.game.Game;
import bg.sofia.uni.fmi.mjt.uno.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.player.account.UserManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientHandler implements Runnable {

    private static final int BUFFER_CAPACITY = 1024;
    private final SocketChannel client;
    private final CommandExecutor commandExecutor;

    public ClientHandler(SocketChannel client, CommandExecutor commandExecutor) {
        this.client = client;
        this.commandExecutor = commandExecutor;
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
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
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
            ByteBuffer buffer = ByteBuffer.wrap((message + "\n").getBytes());
            client.write(buffer);
        } catch (IOException e) {
            System.err.println("Error sending message to client: " + e.getMessage());
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
            System.err.println("Error disconnecting client: " + e.getMessage());
        }
    }
}