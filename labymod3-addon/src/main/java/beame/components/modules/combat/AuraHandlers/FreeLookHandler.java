package beame.components.modules.combat.AuraHandlers;

import beame.util.IMinecraft;
import events.Event;
import events.impl.player.LookEvent;
import events.impl.player.RotationEvent;
import lombok.Getter;
import net.minecraft.util.math.MathHelper;

public class FreeLookHandler extends Handler implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d 5J1hUfMw
    public FreeLookHandler() { }

    @Getter
    private static boolean active;
    @Getter
    private static float freeYaw, freePitch;

    @Override
    public void event(Event event) {
        if(event instanceof LookEvent) {
            LookEvent le = (LookEvent) event;

            if (active) {
                rotateTowards(le.getYaw(), le.getPitch());
                le.setCancel(true);
            }
        }
        if(event instanceof RotationEvent) {
            RotationEvent re = (RotationEvent) event;

            if (active) {
                re.setYaw(freeYaw);
                re.setPitch(freePitch);
            } else {
                freeYaw = re.getYaw();
                freePitch = re.getPitch();
            }
        }
    }

    public static void setActive(boolean state) {
        if (active != state) {
            active = state;
            resetRotation();
        }
    }

    private void rotateTowards(double yaw, double pitch) {
        double d0 = pitch * 0.15D;
        double d1 = yaw * 0.15D;
        freePitch = (float) ((double) freePitch + d0);
        freeYaw = (float) ((double) freeYaw + d1);
        freePitch = MathHelper.clamp(freePitch, -90.0F, 90.0F);
    }

    private static void resetRotation() {
        mc.player.rotationYaw = freeYaw;
        mc.player.rotationPitch = freePitch;
    }
}
