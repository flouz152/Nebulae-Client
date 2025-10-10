package mdk.by.ghostbitbox.util.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

public final class AnimationMath {

    private AnimationMath() {
    }

    public static double deltaTime() {
        int fps = Minecraft.getDebugFPS();
        return fps > 0 ? 1.0d / fps : 1.0d;
    }

    public static float fast(float end, float start, float multiple) {
        float delta = MathHelper.clamp((float) (deltaTime() * multiple), 0.0f, 1.0f);
        return (1.0f - delta) * end + delta * start;
    }
}
