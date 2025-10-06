package beame.components.modules.combat.AuraHandlers;

import events.Event;
import events.impl.player.LookEvent;
import events.impl.player.RotationEvent;
import net.minecraft.util.math.MathHelper;

public class FreeLookHandler extends Handler {
    private static boolean active;
    private static float freeYaw;
    private static float freePitch;

    @Override
    public void event(Event event) {
        if (event instanceof LookEvent lookEvent) {
            if (active) {
                rotateTowards(lookEvent.getYaw(), lookEvent.getPitch());
                lookEvent.setCancel(true);
            }
            return;
        }

        if (event instanceof RotationEvent rotationEvent) {
            if (active) {
                rotationEvent.setYaw(freeYaw);
                rotationEvent.setPitch(freePitch);
            } else {
                freeYaw = rotationEvent.getYaw();
                freePitch = rotationEvent.getPitch();
            }
        }
    }

    public static boolean isActive() {
        return active;
    }

    public static float getFreeYaw() {
        return freeYaw;
    }

    public static float getFreePitch() {
        return freePitch;
    }

    public static void setActive(boolean state) {
        if (active != state) {
            active = state;
            resetRotation();
        }
    }

    private void rotateTowards(double yaw, double pitch) {
        freePitch = MathHelper.clamp(freePitch + (float) (pitch * 0.15D), -90.0F, 90.0F);
        freeYaw += (float) (yaw * 0.15D);
    }

    private static void resetRotation() {
        if (mc.player != null) {
            mc.player.rotationYaw = freeYaw;
            mc.player.rotationPitch = freePitch;
        }
    }
}
