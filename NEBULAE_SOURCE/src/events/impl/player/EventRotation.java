package events.impl.player;

import events.Event;

public class EventRotation extends Event {
// leaked by itskekoff; discord.gg/sk3d wtRwkBDt

    public float yaw,pitch;

    public EventRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

}
