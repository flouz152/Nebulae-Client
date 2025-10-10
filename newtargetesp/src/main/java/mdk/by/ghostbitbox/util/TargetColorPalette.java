package mdk.by.ghostbitbox.util;

public final class TargetColorPalette {

    private static final float SPEED = 3500.0f;

    private TargetColorPalette() {
    }

    public static int primary() {
        float cycle = ((System.currentTimeMillis() % (long) SPEED) / SPEED);
        return ColorUtil.fromHsb(cycle, 0.65f, 1.0f);
    }

    public static int secondary() {
        float cycle = (((System.currentTimeMillis() + SPEED / 2.0f) % (long) SPEED) / SPEED);
        return ColorUtil.fromHsb(cycle, 0.65f, 0.9f);
    }
}
