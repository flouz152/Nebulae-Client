package mdk.by.ghostbitbox.util;

import net.minecraft.util.math.vector.Vector3d;

public final class MathUtil {

    private MathUtil() {
    }

    public static Vector3d lerp(Vector3d current, Vector3d previous, double partialTicks) {
        double clamped = Math.max(0.0, Math.min(1.0, partialTicks));
        double x = previous.x + (current.x - previous.x) * clamped;
        double y = previous.y + (current.y - previous.y) * clamped;
        double z = previous.z + (current.z - previous.z) * clamped;
        return new Vector3d(x, y, z);
    }
}
