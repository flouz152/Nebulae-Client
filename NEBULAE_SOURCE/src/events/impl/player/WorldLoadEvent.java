package events.impl.player;

import events.Event;
import lombok.Getter;

public class WorldLoadEvent extends Event {
// leaked by itskekoff; discord.gg/sk3d fZJPJpij
    @Getter
    private static final WorldLoadEvent instance = new WorldLoadEvent();
}