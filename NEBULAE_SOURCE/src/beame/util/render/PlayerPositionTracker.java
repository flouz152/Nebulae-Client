package beame.util.render;


import beame.util.IMinecraft;
import beame.util.glu.GLU;
import beame.util.math.MathUtil;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import org.joml.Vector4d;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class PlayerPositionTracker implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d vtgo4WoK
    private static final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private static final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
    private static final Map<Entity, Vector4d> entityPositions = new HashMap<>();

    public static boolean isInView(Entity ent) {
        assert (mc.getRenderViewEntity() != null);
        WorldRenderer.frustum.setCameraPosition(PlayerPositionTracker.mc.getRenderManager().info.getProjectedView().x, PlayerPositionTracker.mc.getRenderManager().info.getProjectedView().y, PlayerPositionTracker.mc.getRenderManager().info.getProjectedView().z);
        return WorldRenderer.frustum.isBoundingBoxInFrustum(ent.getBoundingBox()) || ent.ignoreFrustumCheck;
    }

    public static boolean isInView(Vector3d ent) {
        assert (mc.getRenderViewEntity() != null);
        WorldRenderer.frustum.setCameraPosition(PlayerPositionTracker.mc.getRenderManager().info.getProjectedView().x, PlayerPositionTracker.mc.getRenderManager().info.getProjectedView().y, PlayerPositionTracker.mc.getRenderManager().info.getProjectedView().z);
        return WorldRenderer.frustum.isBoundingBoxInFrustum(new AxisAlignedBB(ent.add(-0.5, -0.5, -0.5), ent.add(0.5, 0.5, 0.5)));
    }

    public static Vector4d updatePlayerPositions(Entity player, float partialTicks) {
        Vector3d projection = PlayerPositionTracker.mc.getRenderManager().info.getProjectedView();
        double x = MathUtil.interpolate(player.getPosX(), player.lastTickPosX, partialTicks);
        double y = MathUtil.interpolate(player.getPosY(), player.lastTickPosY, partialTicks);
        double z = MathUtil.interpolate(player.getPosZ(), player.lastTickPosZ, partialTicks);
        Vector3d size = new Vector3d(player.getBoundingBox().maxX - player.getBoundingBox().minX, player.getBoundingBox().maxY - player.getBoundingBox().minY, player.getBoundingBox().maxZ - player.getBoundingBox().minZ);
        AxisAlignedBB aabb = new AxisAlignedBB(x - size.x / 2.0, y, z - size.z / 2.0, x + size.x / 2.0, y + size.y, z + size.z / 2.0);
        Vector4d position2 = null;
        for (int i = 0; i < 8; ++i) {
            Vector3d vector = new Vector3d(i % 2 == 0 ? aabb.minX : aabb.maxX, i / 2 % 2 == 0 ? aabb.minY : aabb.maxY, i / 4 % 2 == 0 ? aabb.minZ : aabb.maxZ);
            vector = PlayerPositionTracker.project2D(vector.x - projection.x, vector.y - projection.y, vector.z - projection.z);
            if (vector == null || !(vector.z >= 0.0) || !(vector.z < 1.0)) continue;
            if (position2 == null) {
                position2 = new Vector4d(vector.x, vector.y, vector.z, 1.0);
                continue;
            }
            position2.x = Math.min(vector.x, position2.x);
            position2.y = Math.min(vector.y, position2.y);
            position2.z = Math.max(vector.x, position2.z);
            position2.w = Math.max(vector.y, position2.w);
        }
        
        // Store the position for later retrieval
        if (position2 != null) {
            entityPositions.put(player, position2);
        }
        
        return position2;
    }
    
    public static Vector4d getEntityPosition(Entity entity) {
        return entityPositions.get(entity);
    }

    private static Vector3d project2D(double x, double y, double z) {
        GL11.glGetFloatv(2982, modelview);
        GL11.glGetFloatv(2983, projection);
        GL11.glGetIntegerv(2978, viewport);
        if (GLU.gluProject((float)x, (float)y, (float)z, modelview, projection, viewport, vector)) {
            return new Vector3d(vector.get(0) / 2.0f, ((float)mc.getMainWindow().getHeight() - vector.get(1)) / 2.0f, vector.get(2));
        }
        return null;
    }
}

