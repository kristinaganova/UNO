package bg.sofia.uni.fmi.mjt.uno.game;

import bg.sofia.uni.fmi.mjt.uno.game.components.GameMessenger;
import bg.sofia.uni.fmi.mjt.uno.game.components.PlayerRegistry;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.util.ArrayList;
import java.util.List;

public class GameMonitor implements Runnable {
    private final PlayerRegistry playerRegistry;
    private final List<Player> finishedPlayers;
    private final List<Player> spectators;
    private final GameMessenger gameMessenger;
    private boolean isRunning;

    public GameMonitor(PlayerRegistry playerRegistry, GameMessenger gameMessenger) {
        this.playerRegistry = playerRegistry;
        this.finishedPlayers = new ArrayList<>();
        this.spectators = new ArrayList<>();
        this.gameMessenger = gameMessenger;
        this.isRunning = true;
    }

    private static final int SLEEP_TIME = 100;
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

    private synchronized void monitorPlayers() {
        List<Player> activePlayers = playerRegistry.getPlayers();

        for (Player player : activePlayers) {
            if (player.getHandManager().getAllCards().isEmpty() && !finishedPlayers.contains(player)) {
                handleFinishedPlayer(player);
            }
        }

        if (activePlayers.size() == 1) {
            handleGameEnd(activePlayers.get(0));
        }
    }

    private void handleFinishedPlayer(Player player) {
        finishedPlayers.add(player);
        playerRegistry.removePlayer(player);
        gameMessenger.notifyAll(player.getAccount().getUsername() + " has finished the game and is now spectating!");
    }

    private void handleGameEnd(Player winner) {
        gameMessenger.notifyAll("Game over! The winner is " + winner.getAccount().getUsername() + "!");
        finishedPlayers.add(winner);
        playerRegistry.removePlayer(winner);
        stop();
    }

    public void stop() {
        isRunning = false;
    }
}
