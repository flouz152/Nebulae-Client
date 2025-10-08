package beame.components.baritone.api.event.events.type;

/**
 * @author Brady
 * @since 10/11/2018
 */
public interface ICancellable {
// leaked by itskekoff; discord.gg/sk3d MVe21cck

    /**
     * Cancels this event
     */
    void cancel();

    /**
     * @return Whether or not this event has been cancelled
     */
    boolean isCancelled();
}
