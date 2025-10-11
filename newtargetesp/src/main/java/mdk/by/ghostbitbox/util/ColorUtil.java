package mdk.by.ghostbitbox.util;

import java.awt.Color;

public final class ColorUtil {

    private ColorUtil() {
    }

    public static int rgba(int r, int g, int b, int a) {
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    public static int setAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    public static int interpolate(int from, int to, float factor) {
        float clamped = Math.max(0.0f, Math.min(1.0f, factor));
        int fr = (from >> 16) & 0xFF;
        int fg = (from >> 8) & 0xFF;
        int fb = from & 0xFF;
        int fa = (from >> 24) & 0xFF;

        int tr = (to >> 16) & 0xFF;
        int tg = (to >> 8) & 0xFF;
        int tb = to & 0xFF;
        int ta = (to >> 24) & 0xFF;

        int r = Math.round(fr + (tr - fr) * clamped);
        int g = Math.round(fg + (tg - fg) * clamped);
        int b = Math.round(fb + (tb - fb) * clamped);
        int a = Math.round(fa + (ta - fa) * clamped);
        return rgba(r, g, b, a);
    }

    public static float[] toNormalized(int color) {
        return new float[]{
                ((color >> 16) & 0xFF) / 255.0f,
                ((color >> 8) & 0xFF) / 255.0f,
                (color & 0xFF) / 255.0f,
                ((color >> 24) & 0xFF) / 255.0f
        };
    }

    public static int fromHsb(float hue, float saturation, float brightness) {
        int rgb = Color.HSBtoRGB(hue, saturation, brightness);
        return 0xFF000000 | (rgb & 0x00FFFFFF);
    }

    public static int darker(int color, double factor) {
        double scale = Math.max(0.0, Math.min(1.0, factor));
        int a = (color >> 24) & 0xFF;
        int r = (int) Math.max(0.0, ((color >> 16) & 0xFF) * (1.0 - scale));
        int g = (int) Math.max(0.0, ((color >> 8) & 0xFF) * (1.0 - scale));
        int b = (int) Math.max(0.0, (color & 0xFF) * (1.0 - scale));
        return rgba(r, g, b, a);
    }
}
