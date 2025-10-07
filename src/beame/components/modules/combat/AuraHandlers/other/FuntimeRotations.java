/*
package beame.components.modules.combat.AuraHandlers.other;

import beame.Nebulae;
import beame.util.IMinecraft;
import beame.util.math.MathUtil;
import beame.util.math.SensUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;


public class FuntimeRotations implements IMinecraft {
    private Vector2f lastRotation = Vector2f.ZERO;
    private float lastSpeed = 0.5f;

    public Vector2f calculateRotation(Vector2f current, LivingEntity target, float yawDelta, float pitchDelta, float speed) {
        Vector3d targetVec = target.getPositionVec()
                .add(0, target.getEyeHeight() / 2, 0)
                .subtract(mc.player.getEyePosition(mc.getRenderPartialTicks()))
                .normalize();

        float targetYaw = (float) Math.toDegrees(Math.atan2(-targetVec.x, targetVec.z));
        float targetPitch = (float) MathHelper.clamp(-Math.toDegrees(Math.atan2(targetVec.y, Math.hypot(targetVec.x, targetVec.z))), -90F, 90F);

        float speedValue = MathUtil.random(
                0.1f,
               0.7f
        );

        float yaw = wrapLerp(speedValue, current.x, targetYaw);
        float pitch = wrapLerp(speedValue * 0.8f, current.y, targetPitch);


        if (Nebulae.getHandler().getModuleList().aura.getHitTick() == 0) {
            float snapFactor = 0.6f + (float)Math.random() * 1;
            yaw = current.x + (targetYaw - current.x) * snapFactor;
            pitch = current.y + (targetPitch - current.y) * snapFactor;
        }

        yaw += (Math.random() * 8 - 4);
        pitch += (Math.random() * 4 - 2);

        float gcd = SensUtil.getGCDValue();
        yaw -= yaw % gcd;
        pitch -= pitch % gcd;

        return new Vector2f(yaw, pitch);
    }

    private float wrapLerp(float step, float input, float target) {
        return input + step * MathHelper.wrapDegrees(target - input);
    }
}
*/
// leaked by itskekoff; discord.gg/sk3d JjqmZabE
