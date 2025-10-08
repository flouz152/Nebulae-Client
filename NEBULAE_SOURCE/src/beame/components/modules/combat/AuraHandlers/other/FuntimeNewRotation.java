/*
package fun.eternityclient.utils.math.rotations.funtime;

import fun.eternityclient.modules.impl.combat.Aura;
import fun.eternityclient.utils.Util;
import beame.util.math.GCDUtils;
import fun.eternityclient.utils.math.MathUtils;
import fun.eternityclient.utils.math.RayTraceUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2f;
import java.security.SecureRandom;

public class FuntimeNewRotation implements Util {

    private float yawJitter, pitchJitter;
    @Getter private float yawJitterAnimated, pitchJitterAnimated;
    @Getter private SecureRandom secureRandom = new SecureRandom();
    @Getter @Setter private boolean raycasted = false, reverse = false;
    private Vec3 interpolatedSelfPos = Vec3.ZERO;

    private float randomizeValue(float min, float max) {
        return min + (max - min) * (float) Math.pow(secureRandom.nextFloat(), 1.2 + secureRandom.nextFloat() * 0.3);
    }

    public void randomizeSeed() {
        this.secureRandom = new SecureRandom();
    }

    public void interpolateThis(Vector2f vector2f, Vector2f prevRotation, Vector2f setter) {
        final float mouseSensitivity = mc.options.sensitivity().get().floatValue();
        float yaw = Mth.lerp(setter == null ? mouseSensitivity * this.randomizeValue(0.65f, 0.69f) : this.randomizeValue(0.55f, 0.69f), prevRotation.x, Mth.wrapDegrees(vector2f.x - prevRotation.x) + prevRotation.x);
        float pitch = Mth.clamp(Mth.lerp(mouseSensitivity * this.randomizeValue(0.75f, 0.82f), prevRotation.y, vector2f.y), -89, 88);
        prevRotation.x = GCDUtils.getFixedRotation(yaw);
        prevRotation.y = GCDUtils.getFixedRotation(pitch);
    }

    public void onEnable() {
        this.randomizeSeed();
        this.interpolatedSelfPos = Vec3.ZERO;
    }

    private float getIDKNahui2(double d) {
        return (float)(d * (Mth.idkLol));
    }

    public static double atan2(double y, double x) {
        if (Double.isNaN(x) || Double.isNaN(y)) {
            return Double.NaN;
        }

        return Mth.atan2(y, x);
    }

    public Vector2f pidor2(Vec3 aYc2) {
        return new Vector2f(Mth.wrapDegrees(this.getIDKNahui2(atan2(aYc2.z, aYc2.x)) - 90.0f), -this.getIDKNahui2(atan2(aYc2.y, Math.hypot(aYc2.x, aYc2.z))));
    }

    public Vector2f pidor1(Vector2f aYb2, Vec3 aYc2) {
        Vector2f aYb3 = pidor2(aYc2);
        return new Vector2f(Mth.wrapDegrees(aYb3.x - aYb2.x), aYb3.y - aYb2.y);
    }

    public void f3(LivingEntity ajE2, Vector2f aYb2, boolean snap, float f) {
        this.raycasted = false;

        if (mc.player.tickCount % this.secureRandom.nextInt(4, 8) == 0) {
            this.yawJitter = this.randomizeValue(-50, 50);
        }

        if (mc.player.tickCount % this.secureRandom.nextInt(2, 4) == 0) {
            this.pitchJitter = this.randomizeValue(-4, 4);
        }

        this.yawJitterAnimated = Mth.lerp(this.secureRandom.nextFloat(0.15f, 0.45f), this.yawJitterAnimated, this.yawJitter);
        this.pitchJitterAnimated = Mth.lerp(this.secureRandom.nextFloat(0.05f, 0.25f), this.pitchJitterAnimated, this.pitchJitter);

        final Vec3 rotateVector = this.f7(ajE2, aYb2, f);
        final Vector2f aYb3 = pidor1(aYb2, rotateVector);
        final float pitch = (aYb2.y + aYb3.y);

        interpolateThis(
                new Vector2f(
                        (aYb2.x + aYb3.x) + (snap ? 0 : this.yawJitterAnimated),
                        pitch + (snap ? 0 : this.pitchJitterAnimated)
                ),
                aYb2,
                aYb2
        );

        this.raycasted = (RayTraceUtils.getMouseOver(ajE2, aYb2.x, aYb2.y, (Aura.distance.getNum())) == ajE2);
    }

    public Vec3 f7(LivingEntity ajE2, Vector2f aYb2, float f) {
        final Vec3 point = new Vec3(ajE2.position().x, ajE2.position().y, ajE2.position().z);
        point.y += ajE2.getBbHeight() / 2.f;

        final Vec3 intSelfPos = mc.player.getEyePosition();

        if (this.interpolatedSelfPos == Vec3.ZERO) this.interpolatedSelfPos = mc.player.getEyePosition().add(this.secureRandom.nextGaussian() * 0.6, this.secureRandom.nextGaussian() * 0.6, this.secureRandom.nextGaussian() * 0.6);
        this.interpolatedSelfPos = new Vec3(
                MathUtils.interpolate(this.interpolatedSelfPos.x, intSelfPos.x, this.randomizeValue(0.05f, 0.25f)),
                MathUtils.interpolate(this.interpolatedSelfPos.y, intSelfPos.y, this.randomizeValue(0.75f, 0.85f)),
                MathUtils.interpolate(this.interpolatedSelfPos.z, intSelfPos.z, this.randomizeValue(0.09f, 0.2f))
        );

        return point.subtract(this.interpolatedSelfPos);
    }

}*/
// leaked by itskekoff; discord.gg/sk3d t8419uCk
