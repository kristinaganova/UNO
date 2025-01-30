package bg.sofia.uni.fmi.mjt.uno.game.components.monitoring;

import bg.sofia.uni.fmi.mjt.uno.game.components.GameMessenger;
import bg.sofia.uni.fmi.mjt.uno.game.components.PlayerRegistry;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameMonitor implements Runnable {
    private final PlayerRegistry playerRegistry;
    private final GameMessenger gameMessenger;
    private boolean isRunning;

    public GameMonitor(PlayerRegistry playerRegistry, GameMessenger gameMessenger) {
        this.playerRegistry = playerRegistry;
        this.gameMessenger = gameMessenger;
        this.isRunning = true;
    }

    private static final int SLEEP_TIME = 1000;
    @Override
    public void run() {
        while (isRunning) {
            try {
                monitorPlayers();
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("GameMonitor interrupted: " + e.getMessage());
            }
        }
    }

    private void monitorPlayers() {
        List<Player> activePlayers = new ArrayList<>(playerRegistry.getPlayers());
        Iterator<Player> iterator = activePlayers.iterator();

        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (player.getHandManager().getAllCards().isEmpty() &&
                    !playerRegistry.getFinishedPlayers().contains(player)) {
                handleFinishedPlayer(player);
                iterator.remove();
            }
        }

        if (activePlayers.size() == 1) {
            Player lastPlayer = activePlayers.get(0);
            handleGameEnd(lastPlayer);
            lastPlayer.getCurrentGame().endGame();
        }
    }

    private synchronized void handleFinishedPlayer(Player player) {
        if (playerRegistry.getFinishedPlayers().contains(player)) {
            return;
        }
        gameMessenger.notifyAll(player.getAccount().getUsername() + " has finished the game");
        playerRegistry.markPlayerAsFinished(player);
    }

    private void handleGameEnd(Player winner) {
        if (winner == null) {
            throw new IllegalArgumentException("Winner cannot be null");
        }

        synchronized (playerRegistry) {
            if (!playerRegistry.getFinishedPlayers().contains(winner)) {
                playerRegistry.markPlayerAsFinished(winner);
            }

            gameMessenger.notifyAll("Game over! The winner is " + winner.getAccount().getUsername() + "!");
            playerRegistry.removePlayer(winner);
            stop();
        }
    }

    public void stop() {
        isRunning = false;
        Thread.currentThread().interrupt();
    }

}