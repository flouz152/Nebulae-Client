package events.impl.player;

import events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public class EventInput extends Event {
// leaked by itskekoff; discord.gg/sk3d 8xld2OXv
    private float forward, strafe;
    private boolean jump, sneak;
    private double sneakSlow;



}
