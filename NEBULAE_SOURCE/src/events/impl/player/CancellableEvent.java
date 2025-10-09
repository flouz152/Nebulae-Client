package events.impl.player;

import events.Event;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CancellableEvent extends Event {
// leaked by itskekoff; discord.gg/sk3d Xx4OLugh

    private boolean cancelled;

    public void setCancelled() {
        this.cancelled = true;
    }

    public void cancel() {
        this.cancelled = true;
    }

}
