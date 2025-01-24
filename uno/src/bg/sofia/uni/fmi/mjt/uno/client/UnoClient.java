package bg.sofia.uni.fmi.mjt.uno.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class UnoClient {

    private static final int BUFFER_CAPACITY = 1024;
    private final SocketChannel socketChannel;
    private final ByteBuffer buffer;

    public UnoClient(String host, int port) throws IOException {
        this.socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
        this.socketChannel.configureBlocking(false);
        this.buffer = ByteBuffer.allocate(BUFFER_CAPACITY);

        System.out.println("Connected to server at " + host + ":" + port);
    }

    public void start() {
        Thread listenerThread = new Thread(this::listenForMessages);
        listenerThread.setDaemon(true);
        listenerThread.start();

        handleUserInput();
    }

    private void listenForMessages() {
        try {
            while (true) {
                buffer.clear();
                int bytesRead = socketChannel.read(buffer);
                if (bytesRead > 0) {
                    buffer.flip();
                    String message = new String(buffer.array(), 0, buffer.limit()).trim();
                    System.out.println("\nServer: " + message);
                    System.out.print("> "); // Re-print the prompt for user input
                } else if (bytesRead == -1) {
                    System.out.println("Disconnected from server.");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error while receiving messages: " + e.getMessage());
        }
    }

    private void handleUserInput() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                String command = scanner.nextLine();

                if ("exit".equalsIgnoreCase(command)) {
                    System.out.println("Exiting...");
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

    private void sendMessage(String message) throws IOException {
        ByteBuffer writeBuffer = ByteBuffer.wrap((message + "\n").getBytes());
        socketChannel.write(writeBuffer);
    }

    private void closeConnection() {
        try {
            socketChannel.close();
            System.out.println("Connection closed.");
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        final String HOST = "localhost";
        final int PORT = 1503;

        try {
            UnoClient client = new UnoClient(HOST, PORT);
            client.start();
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        }
    }
}
