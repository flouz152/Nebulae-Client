package beame.util.math;

import beame.util.IMinecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.function.Predicate;

public class RaytraceUtility implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d zBzhp1Xh
    public static Entity getMouseOver(Entity target, float yaw2, float pitch2, double distance) {
        Entity entity = mc.getRenderViewEntity();
        if (entity != null && mc.world != null) {
            AxisAlignedBB axisalignedbb;
            RayTraceResult objectMouseOver = null;
            boolean flag = distance > 3.0;
            Vector3d startVec = entity.getEyePosition(1.0f);
            Vector3d directionVec = getVectorForRotation(pitch2, yaw2);
            Vector3d endVec = startVec.add(directionVec.x * distance, directionVec.y * distance, directionVec.z * distance);
            EntityRayTraceResult entityraytraceresult = rayTraceEntities(entity, startVec, endVec, axisalignedbb = target.getBoundingBox().grow(target.getCollisionBorderSize()), p_lambda$getMouseOver$0_0_ -> !p_lambda$getMouseOver$0_0_.isSpectator() && p_lambda$getMouseOver$0_0_.canBeCollidedWith(), distance);
            if (entityraytraceresult != null) {
                if (flag && startVec.distanceTo(startVec) > distance) {
                    objectMouseOver = BlockRayTraceResult.createMiss(startVec, null, new BlockPos(startVec));
                }
                if (distance < distance || objectMouseOver == null) {
                    objectMouseOver = entityraytraceresult;
                }
            }
            if (objectMouseOver == null) {
                return null;
            }
            try {
                return ((EntityRayTraceResult)objectMouseOver).getEntity();
            } catch (ClassCastException e) {
                return null;
            }
        }
        return null;
    }

    public static EntityRayTraceResult rayTraceEntities(Entity shooter, Vector3d startVec, Vector3d endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter, double distance) {
        World world = shooter.world;
        double closestDistance = distance;
        Entity entity = null;
        Vector3d closestHitVec = null;
        for (Entity entity1 : world.getEntitiesInAABBexcluding(shooter, boundingBox, filter)) {
            AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow(entity1.getCollisionBorderSize());
            Optional<Vector3d> optional = axisalignedbb.rayTrace(startVec, endVec);
            if (axisalignedbb.contains(startVec)) {
                if (!(closestDistance >= 0.0)) continue;
                entity = entity1;
                closestHitVec = startVec;
                closestDistance = 0.0;
                continue;
            }
            if (!optional.isPresent()) continue;
            Vector3d vector3d1 = optional.get();
            double d3 = startVec.distanceTo(optional.get());
            if (!(d3 < closestDistance) && closestDistance != 0.0) continue;
            boolean flag1 = false;
            if (!flag1 && entity1.getLowestRidingEntity() == shooter.getLowestRidingEntity()) {
                if (closestDistance != 0.0) continue;
                entity = entity1;
                closestHitVec = vector3d1;
                continue;
            }
            entity = entity1;
            closestHitVec = vector3d1;
            closestDistance = d3;
        }
        return entity == null ? null : new EntityRayTraceResult(entity, closestHitVec);
    }

    public static RayTraceResult rayTrace(double rayTraceDistance, float yaw2, float pitch2, Entity entity) {
        Vector3d startVec = mc.player.getEyePosition(1.0f);
        Vector3d directionVec = getVectorForRotation(pitch2, yaw2);
        Vector3d endVec = startVec.add(directionVec.x * rayTraceDistance, directionVec.y * rayTraceDistance, directionVec.z * rayTraceDistance);
        return mc.world.rayTraceBlocks(new RayTraceContext(startVec, endVec, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, entity));
    }

    public static Vector3d getVectorForRotation(float pitch2, float yaw2) {
        float yawRadians = -yaw2 * ((float)Math.PI / 180) - (float)Math.PI;
        float pitchRadians = -pitch2 * ((float)Math.PI / 180);
        float cosYaw = MathHelper.cos(yawRadians);
        float sinYaw = MathHelper.sin(yawRadians);
        float cosPitch = -MathHelper.cos(pitchRadians);
        float sinPitch = MathHelper.sin(pitchRadians);
        return new Vector3d(sinYaw * cosPitch, sinPitch, cosYaw * cosPitch);
    }
}
