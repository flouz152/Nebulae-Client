package events.impl.player;

import events.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EventNoSlow extends Event {
// leaked by itskekoff; discord.gg/sk3d CQWz1Uh1
    private final float f,s;
}
