package events.impl.player;

import events.Event;
import lombok.Getter;

public class TickEvent extends Event {
// leaked by itskekoff; discord.gg/sk3d 18lWW13t
    @Getter
    private static final TickEvent instance = new TickEvent();
}
