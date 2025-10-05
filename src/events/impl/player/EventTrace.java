package events.impl.player;

import events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class EventTrace extends Event {
// leaked by itskekoff; discord.gg/sk3d yF3GqJ4f

    private float yaw, pitch;

}