package bg.sofia.uni.fmi.mjt.uno.server;

import bg.sofia.uni.fmi.mjt.uno.command.CommandExecutor;

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
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CAPACITY);
            int bytesRead = client.read(buffer);

            if (bytesRead == -1) {
                disconnectClient();
                return;
            }

            buffer.flip();
            String message = new String(buffer.array(), 0, buffer.limit()).trim();

            if (message.isEmpty()) {
                return;
            }

            System.out.println("Received: " + message + " from " + client.getRemoteAddress());

            String[] parts = message.split(" ", 2);
            String commandName = parts[0];
            String[] args = parts.length > 1 ? parts[1].split(" ") : new String[0];

            // Debug logs
            System.out.println("Command: " + commandName + ", Args: " + String.join(", ", args));

            String response = commandExecutor.executeCommand(commandName, args, client);

            if (response != null) {
                sendMessage(response);
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
            disconnectClient();
        }
    }

    private void sendMessage(String message) {
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
            System.out.println("Client disconnected: " + client.getRemoteAddress());
            client.close();
        } catch (IOException e) {
            System.err.println("Error disconnecting client: " + e.getMessage());
        }
    }
}
