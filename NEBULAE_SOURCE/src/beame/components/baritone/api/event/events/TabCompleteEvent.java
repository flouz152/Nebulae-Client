package beame.components.baritone.api.event.events;

import beame.components.baritone.api.event.events.type.Cancellable;

/**
 * @author LoganDark
 */
public final class TabCompleteEvent extends Cancellable {
// leaked by itskekoff; discord.gg/sk3d MbS0EXaX

    public final String prefix;
    public String[] completions;

    public TabCompleteEvent(String prefix) {
        this.prefix = prefix;
        this.completions = null;
    }
}
