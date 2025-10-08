package beame.components.baritone.api.event.events.type;

/**
 * @author Brady
 * @since 8/1/2018
 */
public class Cancellable implements ICancellable {
// leaked by itskekoff; discord.gg/sk3d c5AN3sqz

    /**
     * Whether or not this event has been cancelled
     */
    private boolean cancelled;

    @Override
    public final void cancel() {
        this.cancelled = true;
    }

    @Override
    public final boolean isCancelled() {
        return this.cancelled;
    }
}
