package beame.components.modules.combat.AuraHandlers.other;

import beame.util.IMinecraft;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.function.Predicate;

@UtilityClass
public class RayTraceUtil implements IMinecraft {
    public Entity getTargetedEntity(Entity target, float targetYaw, float targetPitch, double distance) {
        Entity viewer = mc.getRenderViewEntity();
        if (viewer == null || mc.world == null) {
            return null;
        }

        Vector3d start = viewer.getEyePosition(mc.getRenderPartialTicks());
        Vector3d direction = getVectorForRotation(targetPitch, targetYaw);
        Vector3d end = start.add(direction.scale(distance));

        AxisAlignedBB searchBox = target.getBoundingBox().grow(target.getCollisionBorderSize());
        EntityRayTraceResult traced = traceEntities(viewer, start, end, searchBox,
                entity -> !entity.isSpectator() && entity.canBeCollidedWith(),
                distance);
        return traced != null ? traced.getEntity() : null;
    }

    public EntityRayTraceResult traceEntities(Entity shooter, Vector3d start, Vector3d end, AxisAlignedBB bounds,
                                              Predicate<Entity> filter, double distance) {
        World world = shooter.world;
        double closestDistance = distance;
        Entity closestEntity = null;
        Vector3d closestHit = null;

        for (Entity entity : world.getEntitiesInAABBexcluding(shooter, bounds, filter)) {
            AxisAlignedBB entityBox = entity.getBoundingBox().grow(entity.getCollisionBorderSize());
            Optional<Vector3d> clip = entityBox.rayTrace(start, end);

            if (entityBox.contains(start) || clip.isPresent()) {
                double hitDistance = clip.map(start::distanceTo).orElse(0.0D);
                if (hitDistance < closestDistance || closestDistance == 0.0D) {
                    if (entity.getLowestRidingEntity() != shooter.getLowestRidingEntity()) {
                        closestEntity = entity;
                        closestHit = clip.orElse(start);
                        closestDistance = hitDistance;
                    }
                }
            }
        }

        return closestEntity == null ? null : new EntityRayTraceResult(closestEntity, closestHit);
    }

    public RayTraceResult calculateRayTrace(double distance, float yaw, float pitch, Entity entity, boolean ignoreBlocks) {
        Vector3d start = mc.player.getEyePosition(mc.getRenderPartialTicks());
        Vector3d direction = getVectorForRotation(pitch, yaw);
        Vector3d end = start.add(direction.scale(distance));

        RayTraceResult blockTrace = traceBlock(start, end, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE);
        double blockDistance = blockTrace.getHitVec().squareDistanceTo(start);

        AxisAlignedBB expanded = entity.getBoundingBox().expand(direction.scale(distance)).grow(1.0D);
        EntityRayTraceResult entityTrace = ProjectileHelper.rayTraceEntities(entity, start, end, expanded,
                candidate -> !candidate.isSpectator() && candidate.isAlive() && candidate.canBeCollidedWith(),
                distance);

        if (entityTrace != null && (ignoreBlocks || entityTrace.getHitVec().squareDistanceTo(start) < blockDistance)) {
            return entityTrace;
        }

        return blockTrace;
    }

    public boolean rayTraceEntity(float yaw, float pitch, double distance, Entity entity) {
        Vector3d eye = mc.player.getEyePosition(mc.getRenderPartialTicks());
        Vector3d direction = getVectorForRotation(pitch, yaw);
        Vector3d end = eye.add(direction.scale(distance));
        AxisAlignedBB box = entity.getBoundingBox();
        return box.contains(eye) || box.rayTrace(eye, end).isPresent();
    }

    public Vector3d getVectorForRotation(float pitch, float yaw) {
        float yawRad = -yaw * ((float) Math.PI / 180F) - (float) Math.PI;
        float pitchRad = -pitch * ((float) Math.PI / 180F);

        float cosYaw = MathHelper.cos(yawRad);
        float sinYaw = MathHelper.sin(yawRad);
        float cosPitch = -MathHelper.cos(pitchRad);
        float sinPitch = MathHelper.sin(pitchRad);

        return new Vector3d(sinYaw * cosPitch, sinPitch, cosYaw * cosPitch);
    }

    public RayTraceResult traceBlock(Vector3d start, Vector3d end, RayTraceContext.BlockMode blockMode, RayTraceContext.FluidMode fluidMode) {
        return mc.world.rayTraceBlocks(new RayTraceContext(start, end, blockMode, fluidMode, mc.player));
    }
}
