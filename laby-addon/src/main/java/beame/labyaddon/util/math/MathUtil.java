package beame.labyaddon.util.math;

import net.minecraft.util.math.vector.Vector3d;

public final class MathUtil {

    private MathUtil() {
    }

    public static Vector3d interpolate(Vector3d current, Vector3d previous, float partialTicks) {
        double x = previous.x + (current.x - previous.x) * partialTicks;
        double y = previous.y + (current.y - previous.y) * partialTicks;
        double z = previous.z + (current.z - previous.z) * partialTicks;
        return new Vector3d(x, y, z);
    }
}
