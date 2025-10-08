package events.impl.game;

import events.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class EventMouseTick extends Event {
// leaked by itskekoff; discord.gg/sk3d rGBYwvMO

    private int button;
}
