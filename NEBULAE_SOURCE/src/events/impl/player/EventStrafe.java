package events.impl.player;

import events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.vector.Vector3d;

@Getter
@Setter
@AllArgsConstructor
public class EventStrafe extends Event {
// leaked by itskekoff; discord.gg/sk3d WEZKXIqq
    private float friction;
    private Vector3d relative;
    private float yaw;
}
