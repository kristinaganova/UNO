package bg.sofia.uni.fmi.mjt.uno.card.types;

public enum WildCardType {
    PLUS_FOUR("Makes the next player draw 4 cards and lets you pick a color"),
    PICK_COLOR("Lets you pick a color");

    private final String description;

    WildCardType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
