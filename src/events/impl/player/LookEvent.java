package events.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LookEvent extends CancellableEvent {
// leaked by itskekoff; discord.gg/sk3d Tmhbx48S
    private double yaw, pitch;
}