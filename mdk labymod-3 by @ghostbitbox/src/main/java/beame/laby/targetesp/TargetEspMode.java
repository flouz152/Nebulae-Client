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

    public String getConfigKey() {
        return name();
    }

    public static TargetEspMode fromConfigValue(String value) {
        for (TargetEspMode mode : values()) {
            if (mode.name().equalsIgnoreCase(value) || mode.displayName.equalsIgnoreCase(value)) {
                return mode;
            }
        }
        return GHOSTS;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
