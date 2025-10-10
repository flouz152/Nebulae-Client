package beame.labyaddon.util.animation;

import beame.labyaddon.util.IMinecraft;
import net.minecraft.util.math.MathHelper;

public final class AnimationMath implements IMinecraft {

    private AnimationMath() {
    }

    public static double deltaTime() {
        return mc.debugFPS > 0 ? 1.0D / mc.debugFPS : 1.0D;
    }

    public static float fast(float current, float target, float speed) {
        float factor = MathHelper.clamp((float) (deltaTime() * speed), 0.0F, 1.0F);
        return current + (target - current) * factor;
    }
}
