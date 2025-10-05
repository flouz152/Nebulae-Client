package beame.util.math;

import beame.util.IMinecraft;
import lombok.experimental.UtilityClass;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;

@UtilityClass
public class SensUtil implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d 1RC2d7Cs

    public float getSens(float rotation) {
        return getDeltaMouse(rotation) * getGCDValue();
    }

    public float getGCDValue() {
        return (float) (getGCD() * 0.15);
    }

    public float getGCD() {
        double mouseSensitivity = mc.gameSettings.mouseSensitivity;
        return (float) (Math.pow(mouseSensitivity * 0.6F + 0.2F, 3.0D) * 8F);
    }

    public static float getFixedRotation(float angle) {
        return Math.round(angle / (getGCD() * 0.15f)) * (getGCD() * 0.15f);
    }


    public float getDeltaMouse(float delta) {
        return Math.round(delta / getGCDValue());
    }

    public static float getSensitivity(float value) {
        float sensitivity = (float) mc.gameSettings.mouseSensitivity;
        float sensitivityMultiplier = 0.6f;
        float sensitivityOffset = 0.2f;
        float sensitivityScalingFactor = 8.0f;
        float sensitivityFactor = 0.15f;

        float adjustedSensitivity = sensitivity * sensitivityMultiplier + sensitivityOffset;
        float sensitivitySquared = adjustedSensitivity * adjustedSensitivity;
        float sensitivityCubed = sensitivitySquared * adjustedSensitivity;
        float scaledSensitivityCubed = sensitivityCubed * sensitivityScalingFactor;

        return value * scaledSensitivityCubed * sensitivityFactor;
    }

    public static float getSensitivity2(float rot) {
        return getDeltaMouse(rot) * getGCDValue();
    }

    public Vector2f applySensitivityFix(Vector2f currentRotation, Vector2f previousRotation) {
        float greatestCommonDivisor = getGCD();

        float yawDifference = currentRotation.x - previousRotation.x;
        float pitchDifference = currentRotation.y - previousRotation.y;

        float yawRemainder = yawDifference % greatestCommonDivisor;
        float pitchRemainder = pitchDifference % greatestCommonDivisor;

        float correctedYaw = currentRotation.x - yawRemainder;
        float correctedPitch = currentRotation.y - pitchRemainder;

        float clampedPitch = MathHelper.clamp(correctedPitch, -90, 90);
        return new Vector2f(correctedYaw, clampedPitch);
    }

    public float applyMinimalThreshold(float value, float threshold) {
        return Math.abs(value) < threshold ? 0 : value;
    }

}
