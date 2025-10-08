package events.impl.player;


import events.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LookVecEvent extends Event {
// leaked by itskekoff; discord.gg/sk3d MM4THYXn
    private float yaw;
    private float pitch;

    @Data
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class Elytra extends Event {
        private float pitch;
    }
}

