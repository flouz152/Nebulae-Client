package beame.util.color;

import beame.Essence;
import beame.feature.themes.Theme;
import beame.util.math.Interpolator;
import beame.util.math.MathUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.experimental.UtilityClass;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

@UtilityClass
public class ColorUtils {
// leaked by itskekoff; discord.gg/sk3d 6OR4il8C

    public final int green = new Color(64, 255, 64).getRGB();
    public final int yellow = new Color(255, 255, 64).getRGB();
    public final int orange = new Color(255, 128, 32).getRGB();
    public final int red = new Color(255, 64, 64).getRGB();

    public float redf(int c) {
        return red(c) / 255.0f;
    }

    public float greenf(int c) {
        return green(c) / 255.0f;
    }

    public float bluef(int c) {
        return blue(c) / 255.0f;
    }

    public float alphaf(int c) {
        return alpha(c) / 255.0f;
    }


    public static int rgb(int r, int g, int b) {
        return 255 << 24 | r << 16 | g << 8 | b;
    }
    public static float[] rgb(final int color) {
        return new float[]{
                (color >> 16 & 0xFF) / 255f,
                (color >> 8 & 0xFF) / 255f,
                (color & 0xFF) / 255f,
                (color >> 24 & 0xFF) / 255f
        };
    }
    public static int interpolator(int baseColor, int newColor, float factor) {
        int r1 = (baseColor >> 16) & 0xFF;
        int g1 = (baseColor >> 8) & 0xFF;
        int b1 = baseColor & 0xFF;
        int a1 = (baseColor >> 24) & 0xFF;

        int r2 = (newColor >> 16) & 0xFF;
        int g2 = (newColor >> 8) & 0xFF;
        int b2 = newColor & 0xFF;
        int a2 = (newColor >> 24) & 0xFF;

        int r = (int) (r1 + factor * (r2 - r1));
        int g = (int) (g1 + factor * (g2 - g1));
        int b = (int) (b1 + factor * (b2 - b1));
        int a = (int) (a1 + factor * (a2 - a1));

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public float[] getRGBAf(int c) {
        return new float[]{redf(c), greenf(c), bluef(c), alphaf(c)};
    }

    public static int rgba(int r, int g, int b, int a) {
        return a << 24 | r << 16 | g << 8 | b;
    }
    public int swapAlpha(int c, int a) {
        return getColor(red(c), green(c), blue(c), a);
    }

    public int multAlpha(int color, float percent01) {
        return getColor(red(color), green(color), blue(color), Math.round(alpha(color) * percent01));
    }

    public int replAlpha(int color, int alpha) {
        return getColor(red(color), green(color), blue(color), alpha);
    }

    public int replAlpha(int color, float alpha) {
        return getColor(red(color), green(color), blue(color), alpha);
    }

    public static int lightenColor(int color, float factor) {
        Color originalColor = new Color(color);
        int red = (int) (originalColor.getRed() + (255 - originalColor.getRed()) * factor);
        int green = (int) (originalColor.getGreen() + (255 - originalColor.getGreen()) * factor);
        int blue = (int) (originalColor.getBlue() + (255 - originalColor.getBlue()) * factor);
        return new Color(red, green, blue).getRGB();
    }

    public static int r(int color) {
        return color >> 16 & 0xFF;
    }

    public static int g(int color) {
        return color >> 8 & 0xFF;
    }

    public static int b(int color) {
        return color & 0xFF;
    }

    public static int a(int color) {
        return color >> 24 & 0xFF;
    }
    public Color interpolate(Color color1, Color color2, double amount) {
        amount = 1F - amount;
        amount = (float) MathUtil.clamp(0, 1, amount);
        return new Color(
                Interpolator.lerp(color1.getRed(), color2.getRed(), amount),
                Interpolator.lerp(color1.getGreen(), color2.getGreen(), amount),
                Interpolator.lerp(color1.getBlue(), color2.getBlue(), amount),
                Interpolator.lerp(color1.getAlpha(), color2.getAlpha(), amount)
        );
    }

    public int interpolate(int color1, int color2, double amount) {
        amount = (float) MathUtil.clamp(0, 1, amount);
        return getColor(
                Interpolator.lerp(red(color1), red(color2), amount),
                Interpolator.lerp(green(color1), green(color2), amount),
                Interpolator.lerp(blue(color1), blue(color2), amount),
                Interpolator.lerp(alpha(color1), alpha(color2), amount)
        );
    }
    public Color[] genGradientForText(Color color1, Color color2, int length) {
        Color[] gradient = new Color[length];
        for (int i = 0; i < length; i++) {
            double pc = (double) i / (length - 1);
            gradient[i] = interpolate(color1, color2, pc);
        }
        return gradient;
    }
    public int red(int c) {
        return c >> 16 & 0xFF;
    }

    public int green(int c) {
        return c >> 8 & 0xFF;
    }

    public int blue(int c) {
        return c & 0xFF;
    }

    public int alpha(int c) {
        return c >> 24 & 0xFF;
    }

    public int random() {
        return ColorUtils.rgba((int)MathUtil.random(80, 255), (int)MathUtil.random(80, 255), (int)MathUtil.random(80, 255), 255);
    }

    public Color lerp(int speed, int index, Color start, Color end) {
        int angle = (int) (((System.currentTimeMillis()) / speed + index) % 360);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return interpolate(start, end, angle / 360f);
    }
    public static Color lerps(int speed, int index, Color color1, Color color2) {
        float ratio = (float) index / (float) speed;
        int red = (int) (color1.getRed() * (1 - ratio) + color2.getRed() * ratio);
        int green = (int) (color1.getGreen() * (1 - ratio) + color2.getGreen() * ratio);
        int blue = (int) (color1.getBlue() * (1 - ratio) + color2.getBlue() * ratio);
        int alpha = (int) (color1.getAlpha() * (1 - ratio) + color2.getAlpha() * ratio);
        return new Color(red, green, blue, alpha);
    }

    public static int getRed(final int hex) {
        return hex >> 16 & 255;
    }

    public static int getGreen(final int hex) {
        return hex >> 8 & 255;
    }

    public static int getBlue(final int hex) {
        return hex & 255;
    }

    public static int getAlpha(final int hex) {
        return hex >> 24 & 255;
    }

    public static int getColor(int red, int green, int blue, int alpha) {
        int color = 0;
        color |= alpha << 24;
        color |= red << 16;
        color |= green << 8;
        return color |= blue;
    }

    public int getColor(float red, float green, float blue, float alpha) {
        return getColor(Math.round(red * 255), Math.round(green * 255), Math.round(blue * 255), Math.round(alpha * 255));
    }

    public int getColor(int red, int green, int blue, float alpha) {
        return getColor(red, green, blue, Math.round(alpha * 255));
    }

    public static int getColor(int bright) {
        return getColor(bright, bright, bright, 255);
    }

    public static Double interpolate(double oldValue, double newValue, double interpolationValue) {
        return (oldValue + (newValue - oldValue) * interpolationValue);
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue) {
        return interpolate(oldValue, newValue, (float) interpolationValue);
    }

    public int rainbow(int speed, int index, float saturation, float brightness, float opacity) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        float hue = angle / 360f;
        int color = Color.HSBtoRGB(hue, saturation, brightness);
        return getColor(red(color), green(color), blue(color), Math.round(opacity * 255));
    }

    public int fade(int speed, int index, int first, int second) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        angle = angle >= 180 ? 360 - angle : angle;
        return overCol(first, second, angle / 180f);
    }

    public int fade(int index) {
        return fade(4, index, Essence.getHandler().themeManager.getColor(0), Essence.getHandler().themeManager.getColor(0));
    }

    public int overCol(int color1, int color2, float percent01) {
        final float percent = MathHelper.clamp(percent01, 0F, 1F);
        return getColor(
                Interpolator.lerp(red(color1), red(color2), percent),
                Interpolator.lerp(green(color1), green(color2), percent),
                Interpolator.lerp(blue(color1), blue(color2), percent),
                Interpolator.lerp(alpha(color1), alpha(color2), percent)
        );
    }

    public static int interpolateColor(int color1, int color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));

        int red1 = getRed(color1);
        int green1 = getGreen(color1);
        int blue1 = getBlue(color1);
        int alpha1 = getAlpha(color1);

        int red2 = getRed(color2);
        int green2 = getGreen(color2);
        int blue2 = getBlue(color2);
        int alpha2 = getAlpha(color2);

        int interpolatedRed = interpolateInt(red1, red2, amount);
        int interpolatedGreen = interpolateInt(green1, green2, amount);
        int interpolatedBlue = interpolateInt(blue1, blue2, amount);
        int interpolatedAlpha = interpolateInt(alpha1, alpha2, amount);

        return (interpolatedAlpha << 24) | (interpolatedRed << 16) | (interpolatedGreen << 8) | interpolatedBlue;
    }
    public static Color interpolateTwoColors(int speed, int index, Color color1, Color color2, boolean loop) {
        int alpha1 = color1.getAlpha();
        int alpha2 = color2.getAlpha();

        float ratio = (float) (Math.sin((System.currentTimeMillis() + index * speed) / 1000.0) + 1.0) / 2.0f;

        int red = (int) (color1.getRed() * (1 - ratio) + color2.getRed() * ratio);
        int green = (int) (color1.getGreen() * (1 - ratio) + color2.getGreen() * ratio);
        int blue = (int) (color1.getBlue() * (1 - ratio) + color2.getBlue() * ratio);
        int alpha = (int) (alpha1 * (1 - ratio) + alpha2 * ratio);

        return new Color(red, green, blue, alpha);
    }

    public static void setAlphaColor(final int color, final float alpha) {
        final float red = (float) (color >> 16 & 255) / 255.0F;
        final float green = (float) (color >> 8 & 255) / 255.0F;
        final float blue = (float) (color & 255) / 255.0F;
        RenderSystem.color4f(red, green, blue, alpha);
    }


    public static void setColor(int color) {
        setAlphaColor(color, (float) (color >> 24 & 255) / 255.0F);
    }

    public static int toColor(String hexColor) {
        int argb = Integer.parseInt(hexColor.substring(1), 16);
        return setAlpha(argb, 255);
    }
    public static int setAlpha(int color, int alpha) {
        return (color & 0x00ffffff) | (alpha << 24);
    }

    public static float[] rgba(final int color) {
        return new float[] {
                (color >> 16 & 0xFF) / 255f,
                (color >> 8 & 0xFF) / 255f,
                (color & 0xFF) / 255f,
                (color >> 24 & 0xFF) / 255f
        };
    }

    public int multDark(int c, float brpc) {
        return getColor((int) ((float) red(c) * brpc), (int) ((float) green(c) * brpc), (int) ((float) blue(c) * brpc), (int) alpha(c));
    }

    public static int gradient(int start, int end, int index, int speed) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        angle = (angle > 180 ? 360 - angle : angle) + 180;
        int color = interpolate(start, end, MathHelper.clamp(angle / 180f - 1, 0, 1));
        float[] hs = rgba(color);
        float[] hsb = Color.RGBtoHSB((int) (hs[0] * 255), (int) (hs[1] * 255), (int) (hs[2] * 255), null);

        hsb[1] *= 1.5F;
        hsb[1] = Math.min(hsb[1], 1.0f);

        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
    }

    public static int interpolate(int start, int end, float value) {
        float[] startColor = rgba(start);
        float[] endColor = rgba(end);

        return rgba((int) MathUtil.interpolate(startColor[0] * 255, endColor[0] * 255, value),
                (int) MathUtil.interpolate(startColor[1] * 255, endColor[1] * 255, value),
                (int) MathUtil.interpolate(startColor[2] * 255, endColor[2] * 255, value),
                (int) MathUtil.interpolate(startColor[3] * 255, endColor[3] * 255, value));
    }

}
