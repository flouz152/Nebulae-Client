package beame.laby.targetesp;

public enum TargetEspMode {
    GHOSTS("Призраки"),
    CIRCLE("Круг"),
    SQUARE("Квадрат"),
    NEW_SQUARE("Новый квадрат");

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