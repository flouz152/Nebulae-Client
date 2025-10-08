package events.impl.player;

import events.Event;
import lombok.Getter;

public class GameUpdateEvent extends Event {
// leaked by itskekoff; discord.gg/sk3d aYJ3k0V3
    @Getter
    private static final GameUpdateEvent instance = new GameUpdateEvent();
}
