package mdk.by.ghostbitbox.util.animation;

public enum Direction {
    FORWARDS,
    BACKWARDS;

    public Direction opposite() {
        return this == FORWARDS ? BACKWARDS : FORWARDS;
    }
}
