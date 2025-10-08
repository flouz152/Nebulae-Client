package events.impl.player;

import events.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class EventPostMove extends Event {
// leaked by itskekoff; discord.gg/sk3d cX6377pg
    private double horizontalMove;
}
