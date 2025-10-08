package events.impl.player;

import events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SpeedFactorEvent extends Event {
// leaked by itskekoff; discord.gg/sk3d ONTbVJrs
    private float speed;
}