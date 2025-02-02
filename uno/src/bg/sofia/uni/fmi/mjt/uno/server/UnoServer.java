package bg.sofia.uni.fmi.mjt.uno.server;

import bg.sofia.uni.fmi.mjt.uno.server.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.uno.server.command.factory.CommandFactory;
import bg.sofia.uni.fmi.mjt.uno.server.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.server.player.account.UserManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class UnoServer {

    private final Selector selector;
    private final CommandExecutor commandExecutor;
    private final UserManager userManager;
    private static final int BUFFER_SIZE = 1024;

    public UnoServer(int port) throws IOException {
        this.selector = Selector.open();

        GameManager gameManager = GameManager.getInstance();
        this.userManager = UserManager.getInstance();
        this.commandExecutor = new CommandExecutor(new CommandFactory(userManager, gameManager));

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server started on port " + port);
    }

    public void start() throws IOException {
        while (true) {
            selector.select();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();

                try {
                    if (key.isAcceptable()) {
                        acceptConnection((ServerSocketChannel) key.channel());
                    } else if (key.isReadable()) {
                        handleClientRequest(key);
                    }
                } catch (IOException e) {
                    key.cancel();
                    System.err.println("Client connection error: " + e.getMessage());
                }
            }
        }
    }

    private void acceptConnection(ServerSocketChannel serverSocketChannel) throws IOException {
        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        System.out.println("New client connected: " + client.getRemoteAddress());
    }

    private void handleClientRequest(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        try {
            int bytesRead = client.read(buffer);
            if (bytesRead == -1) {
                handleClientDisconnection(client);
                return;
            }

            buffer.flip();
            String message = new String(buffer.array(), 0, buffer.limit()).trim();

            if (!message.isEmpty()) {
                System.out.println("Received from client: " + message);
                processCommand(client, message);
            }

        } catch (IOException e) {
            handleClientDisconnection(client);
        }
    }

    private void processCommand(SocketChannel client, String message) {
        try {
            String[] parts = message.split(" ", 2);
            String commandName = parts[0];
            String[] args = parts.length > 1 ? parts[1].split(" ") : new String[0];

            System.out.println("Executing Command: " + commandName + " Args: " + String.join(", ", args));

            String response = commandExecutor.executeCommand(commandName, args, client);

            if (response != null) {
                sendMessageToClient(client, response);
            }
        } catch (Exception e) {
            System.err.println("Error processing command: " + e.getMessage());
            sendMessageToClient(client, "Error: Failed to execute command.");
        }
    }

    private void sendMessageToClient(SocketChannel client, String message) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap((message + System.lineSeparator()).getBytes());
            client.write(buffer);
            System.out.println("Sent to client: " + message);
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    private void handleClientDisconnection(SocketChannel client) {
        try {
            String username = userManager.getLoggedInUsername(client);
            if (username != null) {
                System.out.println("Client disconnected: " + username);
                userManager.logout(client);
            } else {
                System.out.println("Unlogged client disconnected: " + client.getRemoteAddress());
            }
            client.close();
        } catch (IOException e) {
            System.err.println("Error closing client connection: " + e.getMessage());
        }
    }

    private static final int PORT = 1503;

    public static void main(String[] args) {
        try {
            UnoServer server = new UnoServer(PORT);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
