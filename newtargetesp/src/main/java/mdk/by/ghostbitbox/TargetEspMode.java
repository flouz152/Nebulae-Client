package mdk.by.ghostbitbox;

public enum TargetEspMode {
    GHOSTS("Ghosts"),
    CIRCLE("Circle"),
    SQUARE("Square"),
    NEW_SQUARE("New square");

    private final String displayName;

    TargetEspMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}