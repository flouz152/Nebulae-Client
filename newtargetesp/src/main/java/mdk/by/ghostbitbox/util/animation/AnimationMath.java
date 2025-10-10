package mdk.by.ghostbitbox.util.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

import java.lang.reflect.Field;

public final class AnimationMath {

    private static final Field DEBUG_FPS_FIELD;

    private AnimationMath() {
    }

    static {
        Field debugFps = null;
        try {
            debugFps = Minecraft.class.getDeclaredField("debugFPS");
            debugFps.setAccessible(true);
        } catch (NoSuchFieldException ignored) {
        }
        DEBUG_FPS_FIELD = debugFps;
    }

    public static double deltaTime() {
        Minecraft minecraft = Minecraft.getInstance();
        int fps = 0;

        if (minecraft != null && DEBUG_FPS_FIELD != null) {
            try {
                fps = DEBUG_FPS_FIELD.getInt(null);
            } catch (IllegalAccessException ignored) {
            }
        }

        return fps > 0 ? 1.0d / fps : 1.0d;
    }

    public static float fast(float end, float start, float multiple) {
        float delta = MathHelper.clamp((float) (deltaTime() * multiple), 0.0f, 1.0f);
        return (1.0f - delta) * end + delta * start;
    }
}
