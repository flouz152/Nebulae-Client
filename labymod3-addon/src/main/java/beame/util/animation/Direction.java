package beame.util.animation;

public enum Direction {
// leaked by itskekoff; discord.gg/sk3d os2ZQZld
    FORWARDS,
    BACKWARDS;

    public Direction opposite() {
        if (this == Direction.FORWARDS) {
            return Direction.BACKWARDS;
        } else return Direction.FORWARDS;
    }

}
