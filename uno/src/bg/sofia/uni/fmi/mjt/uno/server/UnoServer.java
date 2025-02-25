package bg.sofia.uni.fmi.mjt.uno.server;

import bg.sofia.uni.fmi.mjt.uno.server.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.uno.server.command.factory.CommandFactory;
import bg.sofia.uni.fmi.mjt.uno.server.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.server.player.account.UserManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UnoServer {

    private final Selector selector;
    private final CommandExecutor commandExecutor;
    private final ExecutorService clientThreadPool;
    private volatile boolean running = true;
    private final UserManager userManager;

    private static final int THREAD_POOL_SIZE = 10;

    public UnoServer(int port) throws IOException {
        this.selector = Selector.open();
        this.clientThreadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

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
        while (running) {
            selector.select();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();

                try {
                    if (key.isAcceptable()) {
                        acceptConnection((ServerSocketChannel) key.channel());
                    } else if (key.isReadable()) {
                        processClientRequest(key);
                    }
                } catch (CancelledKeyException e) {
                    System.err.println("Warning: Attempted to use a cancelled key.");
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

    private void processClientRequest(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        clientThreadPool.execute(new ClientHandler(client, commandExecutor));
    }

    public void stop() {
        running = false;
        selector.wakeup();
        clientThreadPool.shutdown();
    }

    public static void main(String[] args) {
        final int port = 1503;
        try {
            UnoServer server = new UnoServer(port);
            Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

            server.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}