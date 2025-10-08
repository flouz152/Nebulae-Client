package beame.components.baritone.api.event.events;

import beame.components.baritone.api.event.events.type.EventState;

import java.util.function.BiFunction;

public final class TickEventBaritone {
// leaked by itskekoff; discord.gg/sk3d uTQbxPsq

    private static int overallTickCount;

    private final EventState state;
    private final Type type;
    private final int count;

    public TickEventBaritone(EventState state, Type type, int count) {
        this.state = state;
        this.type = type;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public Type getType() {
        return type;
    }

    public EventState getState() {
        return state;
    }

    public static synchronized BiFunction<EventState, Type, TickEventBaritone> createNextProvider() {
        final int count = overallTickCount++;
        return (state, type) -> new TickEventBaritone(state, type, count);
    }

    public enum Type {
        /**
         * When guarantees can be made about
         * the game state and in-game variables.
         */
        IN,
        /**
         * No guarantees can be made about the game state.
         * This probably means we are at the main menu.
         */
        OUT,
    }
}
