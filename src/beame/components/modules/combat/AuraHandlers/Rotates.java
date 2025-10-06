package beame.components.modules.combat.AuraHandlers;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class Rotates {
    private float yaw;
    private float pitch;

    public Rotates() {
    }

    public Rotates(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Rotates(Entity entity) {
        this(entity.rotationYaw, entity.rotationPitch);
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getCameraYaw() {
        return MathHelper.wrapDegrees(yaw);
    }

    public double getDelta(Rotates targetRotation) {
        double yawDelta = MathHelper.wrapDegrees(targetRotation.getYaw() - yaw);
        double pitchDelta = MathHelper.wrapDegrees(targetRotation.getPitch() - pitch);
        return Math.hypot(yawDelta, pitchDelta);
    }

    public static Rotates getReal() {
        return new Rotates(FreeLookHandler.getFreeYaw(), FreeLookHandler.getFreePitch());
    }
}
