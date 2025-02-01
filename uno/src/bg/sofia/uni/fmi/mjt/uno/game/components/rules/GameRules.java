package bg.sofia.uni.fmi.mjt.uno.game.components.rules;

import bg.sofia.uni.fmi.mjt.uno.game.components.PlayerRegistry;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GameRules {
    private final PlayerRegistry playerRegistry;

    public GameRules(PlayerRegistry playerRegistry) {
        if (playerRegistry == null) {
            throw new IllegalArgumentException("playerRegistry cannot be null");
        }
        this.playerRegistry = playerRegistry;
    }

    public boolean isGameOver() {
        return playerRegistry.getPlayers().size() <= 1;
    }

    public synchronized List<Player> calculateRanking() {
        Set<Player> uniqueRanking = new LinkedHashSet<>();

        uniqueRanking.addAll(playerRegistry.getFinishedPlayers());

        playerRegistry.getPlayers().stream()
                .sorted(Comparator.comparingInt(p -> p.getHand().getAllCards().size()))
                .forEach(uniqueRanking::add);

        return new ArrayList<>(uniqueRanking);
    }
}

