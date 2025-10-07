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
        Color start = new Color(startColor, true);
        Color end = new Color(endColor, true);
        int r = (int) (start.getRed() + (end.getRed() - start.getRed()) * amount);
        int g = (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * amount);
        int b = (int) (start.getBlue() + (end.getBlue() - start.getBlue()) * amount);
        int a = (int) (start.getAlpha() + (end.getAlpha() - start.getAlpha()) * amount);
        return new Color(r, g, b, a).getRGB();
    }
}
