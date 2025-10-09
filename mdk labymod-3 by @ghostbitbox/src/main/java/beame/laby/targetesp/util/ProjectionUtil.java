package beame.laby.targetesp.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import org.joml.Vector2d;

public final class ProjectionUtil {

    private ProjectionUtil() {
    }

    public static Vector2d toScreen(double x, double y, double z) {
        Minecraft mc = Minecraft.getInstance();
        Vector3d cameraPosition = mc.getRenderManager().info.getProjectedView();
        Quaternion cameraRotation = mc.getRenderManager().getCameraOrientation().copy();
        cameraRotation.conjugate();

        Vector3f projected = new Vector3f((float) (cameraPosition.x - x), (float) (cameraPosition.y - y), (float) (cameraPosition.z - z));
        projected.transform(cameraRotation);

        if (mc.gameSettings.viewBobbing) {
            Entity renderViewEntity = mc.getRenderViewEntity();
            if (renderViewEntity instanceof PlayerEntity) {
                applyViewBobbing(mc, (PlayerEntity) renderViewEntity, projected);
            }
        }

        double fov = mc.gameRenderer.getFOVModifier(mc.getRenderManager().info, mc.getRenderPartialTicks(), true);
        float halfHeight = mc.getMainWindow().getScaledHeight() / 2.0f;
        float scaleFactor = (float) (halfHeight / (projected.getZ() * Math.tan(Math.toRadians(fov / 2.0f))));
        if (projected.getZ() < 0.0f) {
            float screenX = -projected.getX() * scaleFactor + mc.getMainWindow().getScaledWidth() / 2.0f;
            float screenY = mc.getMainWindow().getScaledHeight() / 2.0f - projected.getY() * scaleFactor;
            return new Vector2d(screenX, screenY);
        }
        return null;
    }

    private static void applyViewBobbing(Minecraft mc, PlayerEntity player, Vector3f projected) {
        float walked = player.distanceWalkedModified;
        float deltaWalked = walked - player.prevDistanceWalkedModified;
        float walkTicks = -(walked + deltaWalked * mc.getRenderPartialTicks());
        float cameraYaw = MathHelper.lerp(mc.getRenderPartialTicks(), player.prevCameraYaw, player.cameraYaw);

        Quaternion pitch = new Quaternion(Vector3f.XP, Math.abs(MathHelper.cos(walkTicks * (float) Math.PI - 0.2F) * cameraYaw) * 5.0F, true);
        pitch.conjugate();
        projected.transform(pitch);

        Quaternion roll = new Quaternion(Vector3f.ZP, MathHelper.sin(walkTicks * (float) Math.PI) * cameraYaw * 3.0F, true);
        roll.conjugate();
        projected.transform(roll);

        Vector3f bob = new Vector3f(
                MathHelper.sin(walkTicks * (float) Math.PI) * cameraYaw * 0.5F,
                -Math.abs(MathHelper.cos(walkTicks * (float) Math.PI) * cameraYaw),
                0.0f
        );
        projected.add(bob);
    }
}
