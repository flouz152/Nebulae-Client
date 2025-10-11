package mdk.by.ghostbitbox.util;

import net.minecraft.entity.Entity;

public class TargetTracker {

    private Entity target;
    private float visibility;
    private int lingerTicks;

    private static final float MAX_DISTANCE = 12.0f;
    private static final int MAX_LINGER_TICKS = 40;

    public void setTarget(Entity target) {
        this.target = target;
        this.lingerTicks = MAX_LINGER_TICKS;
    }

    public void tick(Entity player) {
        if (target != null) {
            if (player == null || target == player || !target.isAlive()) {
                target = null;
            } else {
                if (player.getDistance(target) > MAX_DISTANCE) {
                    lingerTicks = Math.max(0, lingerTicks - 1);
                } else {
                    lingerTicks = MAX_LINGER_TICKS;
                }
                if (lingerTicks == 0) {
                    target = null;
                }
            }
        }

        float desired = target != null ? 1.0f : 0.0f;
        visibility += (desired - visibility) * 0.25f;
        if (visibility < 0.001f) {
            visibility = 0.0f;
        }
    }

    public void clear() {
        target = null;
        visibility = 0.0f;
        lingerTicks = 0;
    }

    public Entity getTarget() {
        return target;
    }

    public float getVisibility() {
        return visibility;
    }
}
