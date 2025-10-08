package events.impl.player;

import events.Event;

public class EventTravel extends Event {
// leaked by itskekoff; discord.gg/sk3d 5lB6ZSaE

    public float speed;

    private boolean pre;

    public EventTravel(float speed, boolean pre) {
        this.speed = speed;
        this.pre = pre;
    }
    public boolean isPre() {
        return pre;
    }

}
