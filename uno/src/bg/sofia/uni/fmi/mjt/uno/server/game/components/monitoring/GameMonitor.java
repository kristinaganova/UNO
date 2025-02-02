package bg.sofia.uni.fmi.mjt.uno.server.game.components.monitoring;

import bg.sofia.uni.fmi.mjt.uno.server.game.components.GameMessenger;
import bg.sofia.uni.fmi.mjt.uno.server.game.components.PlayerRegistry;
import bg.sofia.uni.fmi.mjt.uno.server.player.Player;

import java.util.ArrayList;
import java.util.List;

public class GameMonitor implements Runnable {
    private final PlayerRegistry playerRegistry;
    private final GameMessenger gameMessenger;
    private boolean isRunning;
    private final Object monitorLock;

    public GameMonitor(PlayerRegistry playerRegistry, GameMessenger gameMessenger) {
        this.playerRegistry = playerRegistry;
        this.gameMessenger = gameMessenger;
        this.isRunning = true;
        this.monitorLock = new Object();
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                synchronized (monitorLock) {
                    monitorLock.wait();
                }
                monitorPlayers();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("GameMonitor interrupted: " + e.getMessage());
            }
        }
    }

    public void wakeUp() {
        synchronized (monitorLock) {
            monitorLock.notify();
        }
    }

    private void monitorPlayers() {
        List<Player> activePlayers = new ArrayList<>(playerRegistry.getPlayers());

        for (Player player : activePlayers) {
            if (player.getHandManager().getAllCards().isEmpty() &&
                    !playerRegistry.getFinishedPlayers().contains(player)) {
                System.out.println("[GameMonitor] Player finished: " + player.getAccount().username());
                handleFinishedPlayer(player);
            }
        }

        activePlayers = new ArrayList<>(playerRegistry.getPlayers());
        long playersWithCards = activePlayers.stream()
                .filter(p -> !playerRegistry.getFinishedPlayers().contains(p))
                .count();

        if (playersWithCards == 1) {
            Player lastPlayer = activePlayers.stream()
                    .filter(p -> !playerRegistry.getFinishedPlayers().contains(p))
                    .findFirst()
                    .orElse(null);

            if (lastPlayer != null) {
                System.out.println("[GameMonitor] Last player left: " + lastPlayer.getAccount().username());
                handleGameEnd(lastPlayer);
                lastPlayer.getCurrentGame().endGame();
            }
        }
    }

    private synchronized void handleFinishedPlayer(Player player) {
        if (playerRegistry.getFinishedPlayers().contains(player)) {
            return;
        }
        gameMessenger.notifyAll(player.getAccount().username() + " has finished the game");
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

            System.out.println("[GameMonitor] Game Over! The winner is: " + winner.getAccount().username());
            gameMessenger.notifyAll("Game over! The winner is " + winner.getAccount().username() + "!");

            winner.getCurrentGame().endGame();

            stop();
        }
    }

    public void stop() {
        isRunning = false;
        wakeUp();
    }
}