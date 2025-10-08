package dev.nebulae.targetesp.util;

public final class ColorHelper {

    private ColorHelper() {
    }

    public static int withAlpha(int color, int alpha) {
        return (alpha & 0xFF) << 24 | (color & 0xFFFFFF);
    }

    public static int fromRGBA(int r, int g, int b, int a) {
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    public static int lerp(int start, int end, float progress) {
        progress = clamp(progress, 0.0F, 1.0F);
        int sr = (start >> 16) & 0xFF;
        int sg = (start >> 8) & 0xFF;
        int sb = start & 0xFF;
        int er = (end >> 16) & 0xFF;
        int eg = (end >> 8) & 0xFF;
        int eb = end & 0xFF;
        int r = (int) (sr + (er - sr) * progress);
        int g = (int) (sg + (eg - sg) * progress);
        int b = (int) (sb + (eb - sb) * progress);
        int sa = (start >> 24) & 0xFF;
        int ea = (end >> 24) & 0xFF;
        int a = (int) (sa + (ea - sa) * progress);
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    public static int[] toComponents(int color) {
        return new int[]{
                (color >> 16) & 0xFF,
                (color >> 8) & 0xFF,
                color & 0xFF,
                (color >> 24) & 0xFF
        };
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
