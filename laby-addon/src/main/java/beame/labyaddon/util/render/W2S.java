package beame.labyaddon.util.render;

import beame.labyaddon.util.IMinecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import org.joml.Vector2d;

public final class W2S implements IMinecraft {

    private W2S() {
    }

    public static Vector2d project(double x, double y, double z) {
        Vector3d cameraPos = mc.getRenderManager().info.getProjectedView();
        Quaternion rotation = mc.getRenderManager().getCameraOrientation().copy();
        rotation.conjugate();

        Vector3f pos = new Vector3f((float) (cameraPos.x - x), (float) (cameraPos.y - y), (float) (cameraPos.z - z));
        pos.transform(rotation);

        if (mc.gameSettings.viewBobbing) {
            Entity view = mc.getRenderViewEntity();
            if (view instanceof PlayerEntity player) {
                applyViewBobbing(player, pos);
            }
        }

        double fov = mc.gameRenderer.getFOVModifier(mc.getRenderManager().info, mc.getRenderPartialTicks(), true);
        return toScreen(pos, fov);
    }

    private static void applyViewBobbing(PlayerEntity player, Vector3f vec) {
        float walked = player.distanceWalkedModified;
        float delta = walked - player.prevDistanceWalkedModified;
        float offset = -(walked + delta * mc.getRenderPartialTicks());
        float yaw = MathHelper.lerp(mc.getRenderPartialTicks(), player.prevCameraYaw, player.cameraYaw);

        Quaternion xRot = new Quaternion(Vector3f.XP, Math.abs(MathHelper.cos(offset * (float) Math.PI - 0.2F) * yaw) * 5.0F, true);
        xRot.conjugate();
        vec.transform(xRot);

        Quaternion zRot = new Quaternion(Vector3f.ZP, MathHelper.sin(offset * (float) Math.PI) * yaw * 3.0F, true);
        zRot.conjugate();
        vec.transform(zRot);

        Vector3f translation = new Vector3f(MathHelper.sin(offset * (float) Math.PI) * yaw * 0.5F,
                -Math.abs(MathHelper.cos(offset * (float) Math.PI) * yaw), 0.0F);
        translation.setY(-translation.getY());
        vec.add(translation);
    }

    private static Vector2d toScreen(Vector3f vec, double fov) {
        float halfHeight = window.getScaledHeight() / 2.0F;
        float scale = halfHeight / (vec.getZ() * (float) Math.tan(Math.toRadians(fov / 2.0F)));
        if (vec.getZ() < 0.0F) {
            double screenX = -vec.getX() * scale + window.getScaledWidth() / 2.0F;
            double screenY = window.getScaledHeight() / 2.0F - vec.getY() * scale;
            return new Vector2d(screenX, screenY);
        }
        return null;
    }
}
