package beame.components.baritone.api.event.events;

/**
 * @author Brady
 * @since 1/18/2019
 */
public final class SprintStateEvent {
// leaked by itskekoff; discord.gg/sk3d QcfRtCJX

    private Boolean state;

    public final void setState(boolean state) {
        this.state = state;
    }

    public final Boolean getState() {
        return this.state;
    }
}
