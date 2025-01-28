package bg.sofia.uni.fmi.mjt.uno.server;

import bg.sofia.uni.fmi.mjt.uno.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.uno.command.factory.CommandFactory;
import bg.sofia.uni.fmi.mjt.uno.games.GameManager;
import bg.sofia.uni.fmi.mjt.uno.player.account.UserManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class UnoServer {

    private Selector selector;
    private final CommandExecutor commandExecutor;

    public UnoServer(int port) throws IOException {
        this.selector = Selector.open();

        GameManager gameManager = new GameManager();
        UserManager userManager = new UserManager();
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
        System.out.println("New client connected: " + client.getRemoteAddress());
    }

    private void handleClientRequest(SocketChannel client) {
        ClientHandler clientHandler = new ClientHandler(client, commandExecutor);
        new Thread(clientHandler).start();
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
