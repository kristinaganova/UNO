package bg.sofia.uni.fmi.mjt.uno.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class UnoClient {
    private static final int BUFFER_CAPACITY = 1024;
    private final SocketChannel socketChannel;
    private final ByteBuffer buffer;
    private final Selector selector;
    private volatile boolean running = true;

    public UnoClient(String host, int port) throws IOException {
        this.socketChannel = SocketChannel.open();
        this.socketChannel.configureBlocking(false);
        this.socketChannel.connect(new InetSocketAddress(host, port));

        this.selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ);

        this.buffer = ByteBuffer.allocate(BUFFER_CAPACITY);
        System.out.println("Connecting to server at " + host + ":" + port);

        while (!socketChannel.finishConnect()) {
            // Wait for connection to establish
        }
        System.out.println("Connected!");
    }

    public void start() {
        Thread listenerThread = new Thread(this::listenForMessages);
        listenerThread.setDaemon(true);
        listenerThread.start();

        handleUserInput();
    }

    protected void listenForMessages() {
        try {
            while (running) {
                if (selector.select() > 0) {
                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        keyIterator.remove();

                        if (key.isReadable()) {
                            receiveMessage();
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Lost connection to server: " + e.getMessage());
            running = false;
        }
    }

    private void receiveMessage() throws IOException {
        buffer.clear();
        int bytesRead = socketChannel.read(buffer);

        if (bytesRead > 0) {
            buffer.flip();
            String message = new String(buffer.array(), 0, buffer.limit()).trim();
            if (!message.isEmpty()) {
                System.out.println(System.lineSeparator() + message);
                System.out.print("> ");
            }
        } else if (bytesRead == -1) {
            System.out.println("Server closed the connection.");
            running = false;
            closeConnection();
        }
    }

    private void handleUserInput() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (running) {
                System.out.print("> ");
                if (!scanner.hasNextLine()) break;

                String command = scanner.nextLine().trim();
                if ("exit".equalsIgnoreCase(command)) {
                    System.out.println("Exiting...");
                    running = false;
                    break;
                }

                sendMessage(command);
            }
        } catch (IOException e) {
            System.err.println("Error while sending messages: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    protected void sendMessage(String message) throws IOException {
        ByteBuffer writeBuffer = ByteBuffer.wrap((message + "\n").getBytes());
        socketChannel.write(writeBuffer);
    }

    protected void closeConnection() {
        try {
            running = false;
            socketChannel.close();
            selector.close();
            System.out.println("Connection closed.");
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        final String host = "localhost";
        final int port = 1503;

        try {
            UnoClient client = new UnoClient(host, port);
            client.start();
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        }
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }
}
