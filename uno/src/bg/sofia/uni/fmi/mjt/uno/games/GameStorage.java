package bg.sofia.uni.fmi.mjt.uno.games;

import bg.sofia.uni.fmi.mjt.uno.game.Game;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameStorage {

    private static final String FILE_NAME = "games.ser";

    public void saveGames(Map<String, Game> games) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(games);
        } catch (IOException e) {
            System.err.println("Failed to save games: " + e.getMessage());
        }
    }

    public Map<String, Game> loadGames() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (Map<String, Game>) in.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("No saved games found.");
            return new ConcurrentHashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load games: " + e.getMessage());
            return new ConcurrentHashMap<>();
        }
    }
}
