package events.impl.player;


import events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RotationEvent extends Event {
// leaked by itskekoff; discord.gg/sk3d T00UyJU5
    private float yaw, pitch;
    private float partialTicks;
}