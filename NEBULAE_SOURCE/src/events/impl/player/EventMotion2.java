package events.impl.player;

import events.Event;
import lombok.Getter;
import lombok.Setter;

@Getter
public class EventMotion2 extends Event {
// leaked by itskekoff; discord.gg/sk3d cPfwP4MI

    public static float LAST_YAW, LAST_PITCH;

    private float yaw, pitch;
    @Setter
    private boolean ground;

    public EventMotion2(float yaw, float pitch, boolean ground) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.ground = ground;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;

        if (Math.abs(EventMotion2.LAST_YAW - yaw) > 90) {
            //Chat.debug(EventMotion.LAST_YAW + " - " + yaw + " - " + rock.getModules().get(Aura.class).getTarget());
        }
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
        LAST_PITCH = pitch;
    }

}
