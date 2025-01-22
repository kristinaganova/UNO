package bg.sofia.uni.fmi.mjt.uno.game;

import bg.sofia.uni.fmi.mjt.uno.card.models.Card;
import bg.sofia.uni.fmi.mjt.uno.card.types.CardType;
import bg.sofia.uni.fmi.mjt.uno.player.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GameManager {

    private final Map<String, Game> games;

    public GameManager() {
        this.games = new ConcurrentHashMap<>();
    }

    public synchronized boolean createGame(String gameId, int playersCount) {
        if (games.containsKey(gameId)) {
            return false;
        }
        games.put(gameId, new Game(gameId, playersCount));
        return true;
    }

    public synchronized boolean joinGame(String gameId, Player player) {
        Game game = games.get(gameId);
        if (game == null || game.getState() != GameState.CREATED) {
            return false;
        }
        try {
            game.addPlayer(player);
            return true;
        } catch (IllegalStateException | IllegalArgumentException e) {
            return false;
        }
    }

    public synchronized boolean startGame(String gameId) {
        Game game = games.get(gameId);

        if (!isValidGameToStart(game)) {
            return false;
        }

        try {
            distributeInitialCards(game);
            setFirstDiscardCard(game);
            game.setGameState(GameState.STARTED);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isValidGameToStart(Game game) {
        if (game == null || game.getState() != GameState.CREATED) {
            return false;
        }
        if (game.getPlayers().size() < game.getPlayersCount()) {
            return false;
        }
        return true;
    }

    private void distributeInitialCards(Game game) {
        for (Player player : game.getPlayers()) {
            for (int i = 0; i < Game.START_HAND_CARDS_COUNT; i++) {
                player.addCardToHand(game.getDeck().drawCard());
            }
        }
    }

    private void setFirstDiscardCard(Game game) {
        Card firstCard = game.getDeck().drawCard();

        while (firstCard.getCardType() == CardType.WILD) {
            game.getDeck().discardCard(firstCard);
            firstCard = game.getDeck().drawCard();
        }

        game.getDeck().discardCard(firstCard);
        game.setCurrentColor(firstCard.getColor());
    }

    public synchronized String getGameStatus(String gameId) {
        Game game = games.get(gameId);
        return game != null ? game.getState().toString() : null;
    }

    public synchronized void removeGame(String gameId) {
        Game game = games.get(gameId);
        if (game != null && game.getState() == GameState.FINISHED) {
            games.remove(gameId);
        }
    }

    public synchronized String getGamesByStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status cannot be null or blank.");
        }

        return games.values().stream()
                .filter(game -> {
                    switch (status.toLowerCase()) {
                        case "started":
                            return game.getState() == GameState.STARTED;
                        case "finished":
                            return game.getState() == GameState.FINISHED;
                        case "created":
                            return game.getState() == GameState.CREATED;
                        case "all":
                            return true;
                        default:
                            throw new IllegalArgumentException("Invalid status: " + status);
                    }
                })
                .map(game -> String.format("Game ID: %s, Status: %s, Players: %d/%d",
                        game.getId(), game.getState(), game.getPlayers().size(), game.getPlayersCount()))
                .collect(Collectors.joining("\n"));
    }

    public boolean doesGameExist(String gameId) {
        return games.containsKey(gameId);
    }

    public String getGameSummary(String gameId) {
        return null;
    }
}
