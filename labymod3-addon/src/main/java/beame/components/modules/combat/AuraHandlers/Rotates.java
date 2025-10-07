package beame.components.modules.combat.AuraHandlers;

import beame.util.IMinecraft;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Rotates implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d APhuNb3S

    private float yaw, pitch;

    public Rotates(Entity entity) {
        yaw = entity.rotationYaw;
        pitch = entity.rotationPitch;
    }

    public float getCameraYaw() {
        return MathHelper.wrapDegrees(this.yaw);
    }

    public double getDelta(Rotates targetRotation) {
        double yawDelta = MathHelper.wrapDegrees(targetRotation.getYaw() - yaw);
        double pitchDelta = MathHelper.wrapDegrees(targetRotation.getPitch() - pitch);

        return Math.hypot(Math.abs(yawDelta), Math.abs(pitchDelta));
    }

    public static Rotates getReal() {
        return new Rotates(FreeLookHandler.getFreeYaw(), FreeLookHandler.getFreePitch());
    }


}