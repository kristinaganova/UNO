package bg.sofia.uni.fmi.mjt.uno.card.actioncard;

public enum ActionCardType {
    SKIP("Skips the next player's turn"),
    PLUS_TWO("Makes the next player draw 2 cards"),
    PLUS_FOUR("Makes the next player draw 4 cards and lets you pick a color"),
    REVERSE("Reverses the order of play"),
    PICK_COLOR("Lets you pick a color");

    private final String description;

    ActionCardType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
