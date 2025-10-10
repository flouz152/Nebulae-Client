package beame.labyaddon.util.color;

import java.awt.Color;

public final class ColorUtils {

    private ColorUtils() {
    }

    public static int rgba(int r, int g, int b, int a) {
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    public static int setAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
    }

    public static int interpolateColor(int startColor, int endColor, double progress) {
        double clamped = Math.max(0.0D, Math.min(1.0D, progress));
        int sr = (startColor >> 16) & 0xFF;
        int sg = (startColor >> 8) & 0xFF;
        int sb = startColor & 0xFF;
        int sa = (startColor >> 24) & 0xFF;

        int er = (endColor >> 16) & 0xFF;
        int eg = (endColor >> 8) & 0xFF;
        int eb = endColor & 0xFF;
        int ea = (endColor >> 24) & 0xFF;

        int r = (int) (sr + (er - sr) * clamped);
        int g = (int) (sg + (eg - sg) * clamped);
        int b = (int) (sb + (eb - sb) * clamped);
        int a = (int) (sa + (ea - sa) * clamped);
        return rgba(r, g, b, a);
    }

    public static int multiplyAlpha(int color, float alpha) {
        int currentAlpha = (color >> 24) & 0xFF;
        return setAlpha(color, Math.round(currentAlpha * alpha));
    }

    public static int[] rgba(int color) {
        Color c = new Color(color, true);
        return new int[]{c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()};
    }
}
