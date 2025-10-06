package beame.components.modules.combat.AuraHandlers.other;

import beame.components.modules.combat.AuraHandlers.component.core.combat.Rotation;
import beame.util.IMinecraft;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.joml.Vector4f;

@UtilityClass
public class AuraUtil implements IMinecraft {
    private static final double TARGET_STEP = 0.1D;

    public static Vector3d calculateTargetVector(LivingEntity target) {
        Vector3d targetEyePosition = target.getPositionVec().add(0, target.getEyeHeight() - 0.24, 0);
        return targetEyePosition.subtract(mc.player.getEyePosition(1.0F));
    }

    public Vector3d getClosestVec(Vector3d vec, AxisAlignedBB box) {
        return new Vector3d(
                MathHelper.clamp(vec.getX(), box.minX, box.maxX),
                MathHelper.clamp(vec.getY(), box.minY, box.maxY),
                MathHelper.clamp(vec.getZ(), box.minZ, box.maxZ)
        );
    }

    public Vector3d getClosestVec(Vector3d vec, Entity entity) {
        return getClosestVec(vec, entity.getBoundingBox());
    }

    public Vector3d getClosestVec(Entity entity) {
        Vector3d eyePos = mc.player.getEyePosition(mc.getRenderPartialTicks());
        return getClosestVec(eyePos, entity).subtract(eyePos);
    }

    public double getStrictDistance(Entity entity) {
        return getClosestVec(entity).length();
    }

    public double getStrictDistance(LivingEntity entity) {
        return getClosestVec(entity).length();
    }

    public Vector3d getClosestTargetPoint(Entity entity) {
        if (entity == null || mc.player == null) {
            return Vector3d.ZERO;
        }

        Vector3d eyePos = mc.player.getEyePosition(mc.getRenderPartialTicks());
        AxisAlignedBB shrunkenBox = entity.getBoundingBox().grow(-Math.min(entity.getWidth(), entity.getHeight()) / 4F);
        Vector3d closest = eyePos;
        double bestDistance = Double.MAX_VALUE;

        for (double x = shrunkenBox.minX; x <= shrunkenBox.maxX; x += TARGET_STEP) {
            for (double y = shrunkenBox.minY; y <= shrunkenBox.maxY; y += TARGET_STEP) {
                for (double z = shrunkenBox.minZ; z <= shrunkenBox.maxZ; z += TARGET_STEP) {
                    Vector3d candidate = new Vector3d(x, y, z);
                    Vector2f rotation = RotationUtil.calculate(candidate);
                    RayTraceResult trace = RayTraceUtil.calculateRayTrace(
                            mc.playerController.extendedReach() ? 6.0D : 3.0D,
                            rotation.x,
                            rotation.y,
                            mc.player,
                            false
                    );

                    if (trace instanceof EntityRayTraceResult entityTrace && entityTrace.getEntity().equals(entity)) {
                        double distance = eyePos.distanceTo(candidate);
                        if (distance < bestDistance) {
                            bestDistance = distance;
                            closest = candidate;
                        }
                    }
                }
            }
        }

        if (bestDistance == Double.MAX_VALUE) {
            return getClosestVec(entity.getBoundingBox().getCenter(), entity.getBoundingBox());
        }

        return closest;
    }

    public Vector3d getClosestTargetPoint(Vector3d origin, Entity entity, float shrinkFactor) {
        if (entity == null) {
            return Vector3d.ZERO;
        }

        AxisAlignedBB shrunken = entity.getBoundingBox().grow(-shrinkFactor);
        double closestX = MathHelper.clamp(origin.x, shrunken.minX, shrunken.maxX);
        double closestY = MathHelper.clamp(origin.y, shrunken.minY, shrunken.maxY);
        double closestZ = MathHelper.clamp(origin.z, shrunken.minZ, shrunken.maxZ);
        return new Vector3d(closestX, closestY, closestZ);
    }

    public Vector4f calculateRotation(Entity target) {
        Vector3d vec = getClosestTargetPoint(target).subtract(mc.player.getEyePosition(mc.getRenderPartialTicks()));
        float rawYaw = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90F);
        float rawPitch = (float) (-Math.toDegrees(Math.atan2(vec.y, Math.sqrt(vec.x * vec.x + vec.z * vec.z))));
        float yawDelta = MathHelper.wrapDegrees(rawYaw - mc.player.rotationYaw);
        float pitchDelta = rawPitch - mc.player.rotationPitch;
        return new Vector4f(rawYaw, rawPitch, yawDelta, pitchDelta);
    }

    public Vector4f calculateRotationFromCamera(LivingEntity target) {
        Vector3d vec = getClosestTargetPoint(target).subtract(mc.player.getEyePosition(mc.getRenderPartialTicks()));
        float rawYaw = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90F);
        float rawPitch = (float) (-Math.toDegrees(Math.atan2(vec.y, Math.sqrt(vec.x * vec.x + vec.z * vec.z))));
        float yawDelta = MathHelper.wrapDegrees(rawYaw - Rotation.cameraYaw());
        float pitchDelta = rawPitch - Rotation.cameraPitch();
        return new Vector4f(rawYaw, rawPitch, yawDelta, pitchDelta);
    }

    public double calculateFOV(LivingEntity target) {
        Vector4f rotation = calculateRotation(target);
        return Math.hypot(rotation.z, rotation.w);
    }

    public double calculateFOVFromCamera(LivingEntity target) {
        Vector4f rotation = calculateRotationFromCamera(target);
        return Math.hypot(rotation.z, rotation.w);
    }
}
