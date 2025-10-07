package beame.components.modules.combat.AuraHandlers;


import beame.Essence;
import beame.components.modules.combat.AuraHandlers.other.RayTraceUtil;
import beame.util.IMinecraft;
import beame.util.math.MathUtil;
import beame.util.math.SensUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.entity.LivingEntity;
import java.security.SecureRandom;

public class FuntimeNewRotation implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d k01f8klV

    private float yawJitter, pitchJitter;
    @Getter private float yawJitterAnimated, pitchJitterAnimated;
    @Getter private SecureRandom secureRandom = new SecureRandom();
    @Getter @Setter private boolean raycasted = false, reverse = false;
    private Vector3d interpolatedSelfPos = Vector3d.ZERO;

    private float randomizeValue(float min, float max) {
        return min + (max - min) * (float) Math.pow(secureRandom.nextFloat(), 1.2 + secureRandom.nextFloat() * 0.3);
    }

    public void randomizeSeed() {
        this.secureRandom = new SecureRandom();
    }

    public void interpolateThis(Vector2f targetRotation, Vector2f prevRotation, Vector2f setter) {
        final float mouseSensitivity = (float) mc.gameSettings.mouseSensitivity;
        float yaw = MathHelper.lerp(setter == null ? mouseSensitivity * this.randomizeValue(0.65f, 0.69f) : this.randomizeValue(0.55f, 0.69f),
                prevRotation.x, MathHelper.wrapDegrees(targetRotation.x - prevRotation.x) + prevRotation.x);
        float pitch = MathHelper.clamp(MathHelper.lerp(mouseSensitivity * this.randomizeValue(0.75f, 0.82f), prevRotation.y, targetRotation.y), -89, 88);
        prevRotation.x = SensUtil.getFixedRotation(yaw);
        prevRotation.y = SensUtil.getFixedRotation(pitch);
    }

    public void onEnable() {
        this.randomizeSeed();
        this.interpolatedSelfPos = Vector3d.ZERO;
    }

    public static double atan2(double y, double x) {
        if (Double.isNaN(x) || Double.isNaN(y)) {
            return Double.NaN;
        }

        return Math.atan2(y, x);
    }

    public Vector2f calculateDeltaRotation(Vector3d delta) {
        return new Vector2f(
                MathHelper.wrapDegrees((float) (Math.toDegrees(atan2(delta.z, delta.x)) - 90.0f)),
                (float) -Math.toDegrees(atan2(delta.y, Math.hypot(delta.x, delta.z)))
        );
    }

    public Vector2f calculateAngleDiff(Vector2f currentRotation, Vector3d delta) {
        Vector2f calculated = calculateDeltaRotation(delta);
        return new Vector2f(
                MathHelper.wrapDegrees(calculated.x - currentRotation.x),
                calculated.y - currentRotation.y
        );
    }

    public void f3(LivingEntity target, Vector2f currentRotation, boolean snap, float factor) {
        this.raycasted = false;

        if (mc.player.ticksExisted % this.secureRandom.nextInt(4, 8) == 0) {
            this.yawJitter = this.randomizeValue(-50, 50);
        }

        if (mc.player.ticksExisted % this.secureRandom.nextInt(2, 4) == 0) {
            this.pitchJitter = this.randomizeValue(-4, 4);
        }

        this.yawJitterAnimated = MathHelper.lerp(this.secureRandom.nextFloat() * (0.45f - 0.15f) + 0.15f, this.yawJitterAnimated, this.yawJitter);
        this.pitchJitterAnimated = MathHelper.lerp(this.secureRandom.nextFloat() * (0.25f - 0.05f) + 0.05f, this.pitchJitterAnimated, this.pitchJitter);

        Vector3d rotationVector = this.getVectorToTarget(target, currentRotation, factor);
        Vector2f diff = calculateAngleDiff(currentRotation, rotationVector);

        float pitch = currentRotation.y + diff.y;

        interpolateThis(
                new Vector2f(
                        currentRotation.x + diff.x + (snap ? 0 : yawJitterAnimated),
                        pitch + (snap ? 0 : pitchJitterAnimated)
                ),
                currentRotation,
                currentRotation
        );

        this.raycasted = (RayTraceUtil.getTargetedEntity(target, currentRotation.x, currentRotation.y, Essence.getHandler().getModuleList().getAura().getRange().get() + Essence.getHandler().getModuleList().getAura().getPreRange().get()) == target);
    }

    public Vector3d getVectorToTarget(LivingEntity target, Vector2f currentRotation, float factor) {
        Vector3d targetPos = target.getPositionVec().add(0, target.getHeight() / 2.0, 0);
        Vector3d selfEyePos = mc.player.getEyePosition(1.0f);

        if (interpolatedSelfPos == Vector3d.ZERO) {
            interpolatedSelfPos = selfEyePos.add(
                    secureRandom.nextGaussian() * 0.6,
                    secureRandom.nextGaussian() * 0.6,
                    secureRandom.nextGaussian() * 0.6
            );
        }

        interpolatedSelfPos = new Vector3d(
                MathUtil.interpolate(interpolatedSelfPos.x, selfEyePos.x, this.randomizeValue(0.05f, 0.25f)),
                MathUtil.interpolate(interpolatedSelfPos.y, selfEyePos.y, this.randomizeValue(0.75f, 0.85f)),
                MathUtil.interpolate(interpolatedSelfPos.z, selfEyePos.z, this.randomizeValue(0.09f, 0.2f))
        );

        return targetPos.subtract(interpolatedSelfPos);
    }
}
