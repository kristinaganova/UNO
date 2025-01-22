package bg.sofia.uni.fmi.mjt.uno.server;

import bg.sofia.uni.fmi.mjt.uno.game.GameManager;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UnoServer {

    private Selector selector;
    private Map<SocketChannel, Player> clients;
    private GameManager gameManager;
    private CommandHandler commandHandler;

    private static final int CAPACITY = 1024;

    public UnoServer(int port) throws IOException {
        this.selector = Selector.open();
        this.clients = new HashMap<>();
        this.gameManager = new GameManager();
        this.commandHandler = new CommandHandler(gameManager);

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

                if (key.isAcceptable()) {
                    acceptConnection((ServerSocketChannel) key.channel());
                } else if (key.isReadable()) {
                    handleClientRequest((SocketChannel) key.channel());
                }
            }
        }
    }

    private void acceptConnection(ServerSocketChannel serverSocketChannel) throws IOException {
        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);

        clients.put(client, null);
        System.out.println("New client connected: " + client.getRemoteAddress());
    }

    private void handleClientRequest(SocketChannel client) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(CAPACITY);
            int bytesRead = client.read(buffer);

            if (bytesRead == -1) {
                disconnectClient(client);
                return;
            }

            String message = new String(buffer.array()).trim();
            System.out.println("Received: " + message + " from " + client.getRemoteAddress());

            String response = commandHandler.processCommand(client, message, clients);

            if (response != null) {
                sendMessage(client, response);
            }
        } catch (IOException e) {
            System.err.println("Error handling client request: " + e.getMessage());
            disconnectClient(client);
        }
    }

    private void sendMessage(SocketChannel client, String message) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap((message + "\n").getBytes());
        client.write(buffer);
    }

    private void disconnectClient(SocketChannel client) {
        try {
            System.out.println("Client disconnected: " + client.getRemoteAddress());
            clients.remove(client);
            client.close();
        } catch (IOException e) {
            System.err.println("Error disconnecting client: " + e.getMessage());
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
