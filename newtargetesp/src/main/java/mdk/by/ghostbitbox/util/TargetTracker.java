package mdk.by.ghostbitbox.util;

import net.minecraft.entity.Entity;

public class TargetTracker {

    private Entity target;
    private float visibility;

    public void setTarget(Entity target) {
        this.target = target;
    }

    public void tick(Entity player) {
        if (target != null) {
            if (player == null || target == player || !target.isAlive() || player.getDistance(target) > 5.0f) {
                target = null;
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
    }

    public Entity getTarget() {
        return target;
    }

    public float getVisibility() {
        return visibility;
    }
}
