package beame.util.math;

import beame.util.IMinecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

import static beame.util.math.MathUtil.clamp;

public class AuraUtility implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d HcfweyxD
    public static double getDistanceEyePos(Entity target) {
        Vector3d closestHitboxPoint = getClosestVec(target);
        return mc.player.getEyePosition(1.0f).distanceTo(closestHitboxPoint);
    }

    public static Vector3d getClosestVec(Entity entity) {
        Vector3d eyePosVec = mc.player.getEyePosition(1.0F);
        AxisAlignedBB boundingBox = entity.getBoundingBox();
        return new Vector3d(
                clamp(eyePosVec.getX(), boundingBox.minX, boundingBox.maxX),
                clamp(eyePosVec.getY(), boundingBox.minY, boundingBox.maxY),
                clamp(eyePosVec.getZ(), boundingBox.minZ, boundingBox.maxZ)
        );
    }

    public static double getStrictDistance(Entity entity) {
        return getDistanceEyePos(entity);
    }
}
