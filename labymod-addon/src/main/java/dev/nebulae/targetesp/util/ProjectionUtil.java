package dev.nebulae.targetesp.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import org.joml.Vector2f;

public final class ProjectionUtil {

    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    private ProjectionUtil() {
    }

    public static Vector2f projectTo2D(LivingEntity entity, float partialTicks, double yOffset) {
        ActiveRenderInfo camera = MINECRAFT.getRenderManager().info;
        if (camera == null) {
            return null;
        }

        Vector3d projectedView = camera.getProjectedView();
        double x = MathHelper.lerp(partialTicks, entity.lastTickPosX, entity.getPosX());
        double y = MathHelper.lerp(partialTicks, entity.lastTickPosY, entity.getPosY());
        double z = MathHelper.lerp(partialTicks, entity.lastTickPosZ, entity.getPosZ());

        Vector3f position = new Vector3f((float) (projectedView.x - x), (float) (projectedView.y - (y + yOffset)), (float) (projectedView.z - z));

        Quaternion rotation = camera.getRotation().copy();
        rotation.conjugate();
        position.transform(rotation);

        if (MINECRAFT.gameSettings.viewBobbing) {
            ClientPlayerEntity player = MINECRAFT.player;
            if (player != null) {
                applyViewBobbing(position, player, partialTicks);
            }
        }

        double fov = MINECRAFT.gameRenderer.getFOVModifier(camera, partialTicks, true);
        if (position.getZ() >= 0.0F) {
            return null;
        }

        float halfHeight = MINECRAFT.getMainWindow().getScaledHeight() / 2.0F;
        float scale = halfHeight / (position.getZ() * (float) Math.tan(Math.toRadians(fov / 2.0F)));
        float screenX = -position.getX() * scale + MINECRAFT.getMainWindow().getScaledWidth() / 2.0F;
        float screenY = MINECRAFT.getMainWindow().getScaledHeight() / 2.0F - position.getY() * scale;
        return new Vector2f(screenX, screenY);
    }

    private static void applyViewBobbing(Vector3f position, ClientPlayerEntity player, float partialTicks) {
        float walked = player.distanceWalkedModified;
        float delta = walked - player.prevDistanceWalkedModified;
        float frameWalked = -(walked + delta * partialTicks);
        float cameraYaw = MathHelper.lerp(partialTicks, player.prevCameraYaw, player.cameraYaw);

        Quaternion xRot = new Quaternion(Vector3f.XP, Math.abs(MathHelper.cos(frameWalked * (float) Math.PI - 0.2F) * cameraYaw) * 5.0F, true);
        xRot.conjugate();
        position.transform(xRot);

        Quaternion zRot = new Quaternion(Vector3f.ZP, MathHelper.sin(frameWalked * (float) Math.PI) * cameraYaw * 3.0F, true);
        zRot.conjugate();
        position.transform(zRot);

        Vector3f translation = new Vector3f(
                MathHelper.sin(frameWalked * (float) Math.PI) * cameraYaw * 0.5F,
                -Math.abs(MathHelper.cos(frameWalked * (float) Math.PI) * cameraYaw),
                0.0F
        );
        position.add(translation);
    }
}
