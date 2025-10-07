package beame.components.modules.combat.AuraHandlers.other;

import beame.util.IMinecraft;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

@UtilityClass
public class RotationUtil implements IMinecraft {
    private static final double TO_DEGREES = 180.0F / Math.PI;

    public Vector2f calculate(Vector3d fromVec, Vector3d toVec) {
        Vector3d diff = toVec.subtract(fromVec);
        double distance = Math.hypot(diff.x, diff.z);
        float yaw = (float) (MathHelper.atan2(diff.z, diff.x) * TO_DEGREES) - 90.0F;
        float pitch = (float) (-(MathHelper.atan2(diff.y, distance) * TO_DEGREES));
        return new Vector2f(yaw, pitch);
    }

    public Vector2f calculate(org.joml.Vector3d fromVec, org.joml.Vector3d toVec) {
        org.joml.Vector3d diff = new org.joml.Vector3d(toVec).sub(fromVec);
        double distance = Math.hypot(diff.x, diff.z);
        float yaw = (float) (MathHelper.atan2(diff.z, diff.x) * TO_DEGREES) - 90.0F;
        float pitch = (float) (-(MathHelper.atan2(diff.y, distance) * TO_DEGREES));
        return new Vector2f(yaw, pitch);
    }

    public Vector2f calculate(Entity entity) {
        return calculate(entity.getPositionVec().add(0, entity.getEyeHeight(), 0));
    }

    public Vector2f calculate(Vector3d toVec) {
        return calculate(mc.player.getPositionVec().add(0, mc.player.getEyeHeight(), 0), toVec);
    }

    public Vector2f calculate(Vector3d toVec, Direction face) {
        double x = toVec.x + 0.5D + face.getDirectionVec().getX() * 0.5D;
        double y = toVec.y + 0.5D + face.getDirectionVec().getY() * 0.5D;
        double z = toVec.z + 0.5D + face.getDirectionVec().getZ() * 0.5D;
        return calculate(new Vector3d(x, y, z));
    }

    public float calculateCorrectYawOffset(float yaw) {
        double xDiff = mc.player.getPosX() - mc.player.prevPosX;
        double zDiff = mc.player.getPosZ() - mc.player.prevPosZ;
        float distSquared = (float) (xDiff * xDiff + zDiff * zDiff);
        float renderYawOffset = mc.player.renderYawOffset;
        float offset = renderYawOffset;

        if (distSquared > 0.0025000002F) {
            offset = (float) MathHelper.atan2(zDiff, xDiff) * 180F / (float) Math.PI - 90F;
        }

        if (mc.player.swingProgress > 0F) {
            offset = yaw;
        }

        float yawOffsetDiff = MathHelper.wrapDegrees(yaw - (renderYawOffset + MathHelper.wrapDegrees(offset - renderYawOffset) * 0.3F));
        yawOffsetDiff = MathHelper.clamp(yawOffsetDiff, -75F, 75F);

        renderYawOffset = yaw - yawOffsetDiff;
        if (yawOffsetDiff * yawOffsetDiff > 2500F) {
            renderYawOffset += yawOffsetDiff * 0.2F;
        }

        return renderYawOffset;
    }

    public static float getAngleDifference(float dir, float yaw) {
        float difference = Math.abs(yaw - dir) % 360.0f;
        return difference > 180.0f ? 360.0f - difference : difference;
    }

    public static Vector3d getEyesPos(Entity entity) {
        return entity.getPositionVec().add(0, entity.getEyeHeight(entity.getPose()), 0);
    }

    public static float[] calculateAngle(Vector3d to) {
        return calculateAngle(getEyesPos(mc.player), to);
    }

    public static float[] calculateAngle(Vector3d from, Vector3d to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = MathHelper.sqrt((float) (difX * difX + difZ * difZ));
        float yaw = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0);
        float pitch = (float) MathHelper.clamp(MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist))), -90f, 90f);
        return new float[]{yaw, pitch};
    }

    public Vector3f getDirectionVector(float yaw, float pitch) {
        float yawRadians = (float) Math.toRadians(yaw);
        float pitchRadians = (float) Math.toRadians(pitch);

        float x = -MathHelper.cos(pitchRadians) * MathHelper.sin(yawRadians);
        float y = -MathHelper.sin(pitchRadians);
        float z = MathHelper.cos(pitchRadians) * MathHelper.cos(yawRadians);
        return new Vector3f(x, y, z);
    }

    public float calculateFov(float cameraYaw, float cameraPitch, float targetYaw, float targetPitch) {
        Vector3f cameraDirection = getDirectionVector(cameraYaw, cameraPitch);
        Vector3f targetDirection = getDirectionVector(targetYaw, targetPitch);
        float dotProduct = MathHelper.clamp(cameraDirection.dot(targetDirection), -1.0f, 1.0f);
        return (float) Math.toDegrees(Math.acos(dotProduct));
    }
}
