package beame.laby.targetesp.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import org.joml.Vector2d;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ProjectionUtil {

    private ProjectionUtil() {
    }

    private static final Method GET_FOV_MODIFIER = resolveGetFovModifier();

    public static Vector2d toScreen(double x, double y, double z) {
        Minecraft mc = Minecraft.getInstance();
        Vector3d cameraPosition = mc.getRenderManager().info.getProjectedView();
        Quaternion cameraRotation = mc.getRenderManager().getCameraOrientation().copy();
        cameraRotation.conjugate();

        Vector3f projected = new Vector3f((float) (cameraPosition.x - x), (float) (cameraPosition.y - y), (float) (cameraPosition.z - z));
        projected.transform(cameraRotation);

        float partialTicks = mc.getRenderPartialTicks();

        if (mc.gameSettings.viewBobbing) {
            Entity renderViewEntity = mc.getRenderViewEntity();
            if (renderViewEntity instanceof PlayerEntity) {
                applyViewBobbing(mc, (PlayerEntity) renderViewEntity, projected, partialTicks);
            }
        }

        double fov = resolveFov(mc, partialTicks);
        float halfHeight = mc.getMainWindow().getScaledHeight() / 2.0f;
        float scaleFactor = (float) (halfHeight / (projected.getZ() * Math.tan(Math.toRadians(fov / 2.0f))));
        if (projected.getZ() < 0.0f) {
            float screenX = -projected.getX() * scaleFactor + mc.getMainWindow().getScaledWidth() / 2.0f;
            float screenY = mc.getMainWindow().getScaledHeight() / 2.0f - projected.getY() * scaleFactor;
            return new Vector2d(screenX, screenY);
        }
        return null;
    }

    private static double resolveFov(Minecraft mc, float partialTicks) {
        if (GET_FOV_MODIFIER != null) {
            try {
                return (double) GET_FOV_MODIFIER.invoke(mc.gameRenderer, mc.getRenderManager().info, partialTicks, true);
            } catch (IllegalAccessException | InvocationTargetException ignored) {
                // fall back below when reflection fails at runtime
            }
        }
        return mc.gameSettings.fov;
    }

    private static void applyViewBobbing(Minecraft mc, PlayerEntity player, Vector3f projected, float partialTicks) {
        float walked = player.distanceWalkedModified;
        float deltaWalked = walked - player.prevDistanceWalkedModified;
        float walkTicks = -(walked + deltaWalked * partialTicks);
        float cameraYaw = MathHelper.lerp(partialTicks, player.prevCameraYaw, player.cameraYaw);

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

    private static Method resolveGetFovModifier() {
        return findFovMethod("getFOVModifier");
    }

    private static Method findFovMethod(String name) {
        try {
            Method method = GameRenderer.class.getDeclaredMethod(name, ActiveRenderInfo.class, float.class, boolean.class);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException ex) {
            if ("getFOVModifier".equals(name)) {
                return findFovMethod("func_228268_a_");
            }
            return null;
        }
    }
}