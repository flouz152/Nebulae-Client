/*
package beame.util.math;

import beame.util.IMinecraft;
import lombok.experimental.UtilityClass;

import java.util.concurrent.ThreadLocalRandom;

import static beame.util.IMinecraft.mc;
@UtilityClass
public class GCDUtils implements IMinecraft {

    public static float getFixedRotation(float angle) {
        return Math.round(angle / (getGCD() * 0.15f)) * (getGCD() * 0.15f);
    }

    public static float getGCDValue() {
        return (getGCD() * (0.11f + ThreadLocalRandom.current().nextFloat(0.01f, 0.1f)));
    }

    public static float getGCD() {
        double sensitivity = mc.Gaoptions.sensitivity().get();
        double value = sensitivity * 0.6 + 0.2;
        double result = Math.pow(value, 3) * 8.0;
        return (float) result;
    }

    public static float getDeltaMouse(double delta) {
        return Math.round(delta / getGCDValue());
    }

}
*/
// leaked by itskekoff; discord.gg/sk3d qKZVV34B
