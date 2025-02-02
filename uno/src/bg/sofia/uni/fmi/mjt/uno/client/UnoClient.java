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
    private static final int RECONNECT_DELAY_MS = 5000;
    private static final int MAX_RECONNECT_ATTEMPTS = 5;

    private SocketChannel socketChannel;
    private ByteBuffer buffer;
    private Selector selector;
    private boolean running = true;
    private boolean isConnected = false;

    private final String host;
    private final int port;
    private final Object lock = new Object();

    public UnoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        synchronized (lock) {
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

            isConnected = true;
            lock.notifyAll();
            System.out.println("Connected!");
        }
    }

    public void start() {
        try {
            connect();

            Thread listenerThread = new Thread(this::listenForMessages);
            listenerThread.setDaemon(true);
            listenerThread.start();

            handleUserInput();
        } catch (IOException e) {
            System.err.println("Failed to connect: " + e.getMessage());
            attemptReconnect();
        }
    }

    private void listenForMessages() {
        try {
            while (running) {
                synchronized (lock) {
                    while (!isConnected) {
                        lock.wait();
                    }
                }

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
        } catch (IOException | InterruptedException e) {
            System.err.println("Lost connection to server: " + e.getMessage());
            attemptReconnect();
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
            attemptReconnect();
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

        } finally {
            closeConnection();
        }
    }

    private void sendMessage(String message) {
        synchronized (lock) {
            while (!isConnected) {
                try {
                    System.out.println("Waiting for reconnection...");
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Interrupted while waiting for reconnection.");
                    return;
                }
            }

            try {
                ByteBuffer writeBuffer = ByteBuffer.wrap((message + System.lineSeparator()).getBytes());
                socketChannel.write(writeBuffer);
            } catch (IOException e) {
                System.err.println("Error sending message: " + e.getMessage());
                attemptReconnect();
            }
        }
    }

    private void attemptReconnect() {
        closeConnection();

        System.out.println("Attempting to reconnect...");
        int attempts = 0;

        while (running && attempts < MAX_RECONNECT_ATTEMPTS) {
            try {
                System.out.println("Reconnect attempt " + (attempts + 1) + " of " + MAX_RECONNECT_ATTEMPTS);
                Thread.sleep(RECONNECT_DELAY_MS);
                connect();
                System.out.println("Reconnected successfully!");
                return;
            } catch (IOException | InterruptedException e) {
                attempts++;
                System.err.println("Reconnect attempt " + attempts + " failed, retrying...");
            }
        }

        System.err.println("Max reconnection attempts reached. Giving up.");
        running = false;
    }

    private void closeConnection() {
        synchronized (lock) {
            try {
                isConnected = false;
                if (socketChannel != null && socketChannel.isOpen()) {
                    socketChannel.close();
                }
                if (selector != null) {
                    selector.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        final String host = "localhost";
        final int port = 1503;

        UnoClient client = new UnoClient(host, port);
        client.start();
    }
}
