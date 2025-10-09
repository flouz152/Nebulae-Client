package events.impl.player;

import events.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class EventSlowWalking extends Event {
// leaked by itskekoff; discord.gg/sk3d 2dLa4ssa
    private final float forward, strafe;

}

