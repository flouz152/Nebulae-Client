package events.impl.player;

import events.Event;


public class EventDamage extends Event {
// leaked by itskekoff; discord.gg/sk3d k7arPmri
    private final DamageType damageType;

    public EventDamage(DamageType damageType) {
        this.damageType = damageType;
    }

    public DamageType getDamageType() {
        return this.damageType;
    }

    public enum DamageType {
        FALL,
        ARROW,
        ENDER_PEARL;
    }
}
