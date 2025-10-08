package beame.util.other;

import beame.util.math.Interpolator;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;
@UtilityClass
public class Mathf {
// leaked by itskekoff; discord.gg/sk3d NeILbuJE

    public float clamp01(float x) {
        return (float) clamp(0, 1, x);
    }

    public static double getRandom(double min, double max) {
        if (min == max) {
            return min;
        } else if (min > max) {
            final double d = min;
            min = max;
            max = d;
        }
        return ThreadLocalRandom.current().nextDouble() * (max - min) + min;
    }



    public static float calculateDelta(float a, float b) {
        return a - b;
    }

    public static double round(double target, int decimal) {
        double p = Math.pow(10, decimal);
        return Math.round(target * p) / p;
    }

    public Number round(double num, double increment) {
        if (increment <= 0) {
            throw new IllegalArgumentException("Increment must be greater than zero");
        }
        double roundedValue = Math.round(num / increment) * increment;
        BigDecimal bigDecimal = BigDecimal.valueOf(roundedValue);
        bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    public String formatTime(long millis) {
        long hours = millis / 3600000;
        long minutes = (millis % 3600000) / 60000;
        long seconds = ((millis % 360000) % 60000) / 1000;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public double calcDiff(double a, double b) {
        return a - b;
    }

    public float slerp(float start, float end, float t) {
        t = Math.max(0.0f, Math.min(1.0f, t));
        float startRadians = (float) Math.toRadians(start);
        float endRadians = (float) Math.toRadians(end);

        float dotProduct = (float) Math.cos(startRadians) * (float) Math.cos(endRadians) +
                (float) Math.sin(startRadians) * (float) Math.sin(endRadians);

        float angle = (float) Math.acos(dotProduct);

        if (Math.abs(angle) < 0.001f) {
            return start;
        }

        float factorStart = (float) (Math.sin((1 - t) * angle) / Math.sin(angle));
        float factorEnd = (float) (Math.sin(t * angle) / Math.sin(angle));

        float interpolatedValue = start * factorStart + end * factorEnd;
        return (float) MathHelper.clamp(MathHelper.wrapDegrees(Math.toDegrees(interpolatedValue)), start, end);
    }

    public double round(final double value, final int scale, final double inc) {
        final double halfOfInc = inc / 2.0;
        final double floored = Math.floor(value / inc) * inc;

        if (value >= floored + halfOfInc) {
            return new BigDecimal(Math.ceil(value / inc) * inc)
                    .setScale(scale, RoundingMode.HALF_UP)
                    .doubleValue();
        } else {
            return new BigDecimal(floored)
                    .setScale(scale, RoundingMode.HALF_UP)
                    .doubleValue();
        }
    }

    public double step(final double value, final double steps) {
        double a = ((Math.round(value / steps)) * steps);
        a *= 1000;
        a = (int) a;
        a /= 1000;
        return a;
    }
    public double getDistance(BlockPos pos1, BlockPos pos2) {
        double deltaX = calcDiff(pos1.getX(), pos2.getX());
        double deltaY = calcDiff(pos1.getY(), pos2.getY());
        double deltaZ = calcDiff(pos1.getZ(), pos2.getZ());
        return MathHelper.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }

    public static double clamp(double min, double max, double n) {
        return Math.max(min, Math.min(max, n));
    }

    public static float limit(float current, float inputMin, float inputMax, float outputMin, float outputMax) {
        current = (float) Mathf.clamp(inputMin, inputMax, current);
        float distancePercentage = (current - inputMin) / (inputMax - inputMin);
        return Interpolator.lerp(outputMin, outputMax, distancePercentage);
    }

    public float normalize(float value, float min, float max) {
        return (value - min) / (max - min);
    }

    public double interporate(double p_219803_0_, double p_219803_2_, double p_219803_4_) {
        return p_219803_2_ + p_219803_0_ * (p_219803_4_ - p_219803_2_);
    }

    public float lerp(float min, float max, float delta) {
        return min + (max - min) * delta;
    }

    public float easeOutExpo(float x) {
        return x == 1f ? 1f : (float) (1f - Math.pow(2f, -10f * x));
    }
}
