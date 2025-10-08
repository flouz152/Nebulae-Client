package events.impl.player;

import events.Event;
import lombok.Getter;

public final class WorldChangeEvent extends Event {
// leaked by itskekoff; discord.gg/sk3d b0pueMq8
    @Getter
    private static final WorldChangeEvent instance = new WorldChangeEvent();
}
