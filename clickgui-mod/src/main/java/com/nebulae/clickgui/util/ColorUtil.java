package com.nebulae.clickgui.util;

import java.awt.Color;

public final class ColorUtil {
    private ColorUtil() {
    }

    public static int withAlpha(int color, int alpha) {
        Color c = new Color(color, true);
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha).getRGB();
    }

    public static int rgba(int r, int g, int b, int a) {
        return new Color(r, g, b, a).getRGB();
    }

    public static int lerp(int startColor, int endColor, float amount) {
        amount = AnimationUtil.clamp(amount, 0.0F, 1.0F);
        int r = (int) (Color.red(startColor) + (Color.red(endColor) - Color.red(startColor)) * amount);
        int g = (int) (Color.green(startColor) + (Color.green(endColor) - Color.green(startColor)) * amount);
        int b = (int) (Color.blue(startColor) + (Color.blue(endColor) - Color.blue(startColor)) * amount);
        int a = (int) (Color.alpha(startColor) + (Color.alpha(endColor) - Color.alpha(startColor)) * amount);
        return new Color(r, g, b, a).getRGB();
    }
}
