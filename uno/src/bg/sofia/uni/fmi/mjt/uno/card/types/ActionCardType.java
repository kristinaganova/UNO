package bg.sofia.uni.fmi.mjt.uno.card.types;

public enum ActionCardType {
    SKIP("Skips the next player's turn"),
    PLUS_TWO("Makes the next player draw 2 cards"),
    REVERSE("Reverses the order of play");

    private final String description;

    ActionCardType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
