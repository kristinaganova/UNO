package bg.sofia.uni.fmi.mjt.uno.game.components;

import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.util.ArrayList;
import java.util.List;

public class GameRules {
    private final PlayerRegistry playerRegistry;

    public GameRules(PlayerRegistry playerRegistry) {
        this.playerRegistry = playerRegistry;
    }

    public boolean isGameOver() {
        return playerRegistry.getPlayers().size() <= 1;
    }

    public Player determineWinner() {
        return playerRegistry.getPlayers().stream()
                .min((p1, p2) -> Integer.compare(
                        p1.getHand().getAllCards().size(),
                        p2.getHand().getAllCards().size()))
                .orElse(null);
    }

    public List<Player> calculateRanking(Player winner) {
        List<Player> ranking = new ArrayList<>();

        ranking.addAll(playerRegistry.getFinishedPlayers());

        ranking.addAll(playerRegistry.getPlayers().stream()
                .sorted((p1, p2) -> Integer.compare(
                        p1.getHand().getAllCards().size(),
                        p2.getHand().getAllCards().size()))
                .toList());

        return ranking;
    }

}

