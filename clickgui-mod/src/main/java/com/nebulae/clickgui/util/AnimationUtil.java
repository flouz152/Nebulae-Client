package com.nebulae.clickgui.util;

public final class AnimationUtil {
    private AnimationUtil() {
    }

    public static float fast(float current, float target, float speed) {
        float delta = (float) (getFrameTime() * speed);
        delta = clamp(delta, 0.0F, 1.0F);
        return current + (target - current) * delta;
    }

    public static float lerp(float current, float target, float speed) {
        float delta = (float) (getFrameTime() * speed);
        delta = clamp(delta, 0.0F, 1.0F);
        return current + (target - current) * delta;
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double getFrameTime() {
        int fps = net.minecraft.client.Minecraft.getInstance().getFps();
        if (fps <= 0) {
            return 1.0D;
        }
        return 1.0D / (double) fps;
    }
}
