package beame.laby.targetesp;

import beame.laby.targetesp.util.ColorUtil;
import beame.laby.targetesp.util.HudRenderUtil;
import beame.laby.targetesp.util.MathUtil;
import beame.laby.targetesp.util.ProjectionUtil;
import beame.laby.targetesp.util.TargetColorPalette;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.joml.Vector2d;
import org.lwjgl.opengl.GL11;

public class TargetEspRenderer {

    private static final Minecraft MC = Minecraft.getInstance();

    private static final ResourceLocation QUAD = new ResourceLocation("night/image/target/Quad.png");
    private static final ResourceLocation QUAD_NEW = new ResourceLocation("night/image/target/Quad2.png");
    private static final ResourceLocation GHOST = new ResourceLocation("night/image/glow.png");

    private static final float GHOST_RADIUS = 0.7f;
    private static final float GHOST_WIDTH = 0.4f;
    private static final int GHOST_LENGTH = 24;
    private static final float GHOST_ROTATION_STEP = 0.18f;
    private static final float GHOST_SPEED = 33.0f;
    private static final float CIRCLE_DURATION = 2000.0f;

    private long startTime = System.currentTimeMillis();

    public void drawHud(MatrixStack matrices, Entity target, TargetEspMode mode, float visibility, float partialTicks) {
        if (mode != TargetEspMode.SQUARE && mode != TargetEspMode.NEW_SQUARE) {
            return;
        }

        Vector3d interpolated = MathUtil.lerp(target.getPositionVec(),
                new Vector3d(target.lastTickPosX, target.lastTickPosY, target.lastTickPosZ), partialTicks);
        interpolated = interpolated.add(0.0, target.getHeight() * 0.5, 0.0);
        Vector2d screen = ProjectionUtil.toScreen(interpolated.x, interpolated.y, interpolated.z);
        if (screen == null) {
            return;
        }

        float size = MC.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON ? 90.0f : 60.0f;
        int color = ColorUtil.setAlpha(resolveTargetColor(target), Math.min(255, Math.round(visibility * 255.0f)));
        Vector2f pos = new Vector2f((float) screen.x, (float) screen.y);

        float rotation = (System.currentTimeMillis() % 2000L) / 2000.0f * 360.0f;
        ResourceLocation texture = mode == TargetEspMode.SQUARE ? QUAD : QUAD_NEW;

        GlStateManager.pushMatrix();
        GlStateManager.translatef(pos.x, pos.y, 0.0f);
        GlStateManager.rotatef(rotation, 0.0f, 0.0f, 1.0f);
        GlStateManager.translatef(-pos.x, -pos.y, 0.0f);
        HudRenderUtil.drawImage(matrices, texture, pos.x - size / 2.0f, pos.y - size / 2.0f, size, size, color, 0.0f);
        GlStateManager.popMatrix();
    }

    public void drawWorld(MatrixStack matrices, Entity target, TargetEspMode mode, float visibility, float partialTicks) {
        if (mode == TargetEspMode.GHOSTS) {
            drawGhosts(matrices, target, visibility, partialTicks);
        } else if (mode == TargetEspMode.CIRCLE) {
            drawCircle(matrices, target, visibility, partialTicks);
        }
    }

    private void drawGhosts(MatrixStack matrices, Entity target, float visibility, float partialTicks) {
        matrices.push();
        RenderSystem.pushMatrix();
        RenderSystem.disableLighting();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        RenderSystem.disableCull();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, 0, 1);

        ActiveRenderInfo camera = MC.getRenderManager().info;
        matrices.translate(-camera.getProjectedView().getX(), -camera.getProjectedView().getY(), -camera.getProjectedView().getZ());

        Vector3d interpolated = MathUtil.lerp(target.getPositionVec(),
                new Vector3d(target.lastTickPosX, target.lastTickPosY, target.lastTickPosZ), partialTicks);
        interpolated = interpolated.add(0.2, target.getHeight() * 0.25, 0.0);
        matrices.translate(interpolated.x, interpolated.y, interpolated.z);

        MC.getTextureManager().bindTexture(GHOST);

        float colorVisibility = Math.min(1.0f, visibility);
        int baseColor = resolveTargetColor(target);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        for (int pass = 0; pass < 3; pass++) {
            for (int i = 0; i < GHOST_LENGTH; i++) {
                Quaternion rotation = camera.getRotation().copy();
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);

                double angle = GHOST_ROTATION_STEP * ((System.currentTimeMillis() - startTime) - i * 12.0) / GHOST_SPEED;
                double sin = Math.sin(angle) * GHOST_RADIUS;
                double cos = Math.cos(angle) * GHOST_RADIUS;

                if (pass == 0) {
                    matrices.translate(sin, cos, -cos);
                } else if (pass == 1) {
                    matrices.translate(-sin, sin, -cos);
                } else {
                    matrices.translate(cos, -sin, -sin);
                }

                float size = GHOST_WIDTH;
                matrices.translate(-size / 2.0f, -size / 2.0f, 0.0f);
                matrices.rotate(rotation);
                matrices.translate(size / 2.0f, size / 2.0f, 0.0f);

                int alpha = Math.min(255, Math.round(colorVisibility * (i + 1) * 10.0f));
                int rgba = ColorUtil.setAlpha(baseColor, alpha);
                float[] normalized = ColorUtil.toNormalized(rgba);

                buffer.pos(matrices.getLast().getMatrix(), 0.0f, -size, 0.0f).color(normalized[0], normalized[1], normalized[2], normalized[3]).tex(0.0f, 0.0f).endVertex();
                buffer.pos(matrices.getLast().getMatrix(), -size, -size, 0.0f).color(normalized[0], normalized[1], normalized[2], normalized[3]).tex(0.0f, 1.0f).endVertex();
                buffer.pos(matrices.getLast().getMatrix(), -size, 0.0f, 0.0f).color(normalized[0], normalized[1], normalized[2], normalized[3]).tex(1.0f, 1.0f).endVertex();
                buffer.pos(matrices.getLast().getMatrix(), 0.0f, 0.0f, 0.0f).color(normalized[0], normalized[1], normalized[2], normalized[3]).tex(1.0f, 0.0f).endVertex();
                buffer.finishDrawing();
                WorldVertexBufferUploader.draw(buffer);

                matrices.translate(-size / 2.0f, -size / 2.0f, 0.0f);
                rotation.conjugate();
                matrices.rotate(rotation);
                matrices.translate(size / 2.0f, size / 2.0f, 0.0f);

                if (pass == 0) {
                    matrices.translate(-sin, -cos, cos);
                } else if (pass == 1) {
                    matrices.translate(sin, -sin, cos);
                } else {
                    matrices.translate(-cos, sin, sin);
                }
            }
        }

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.enableAlphaTest();
        RenderSystem.depthMask(true);
        RenderSystem.popMatrix();
        matrices.pop();
    }

    private void drawCircle(MatrixStack matrices, Entity target, float visibility, float partialTicks) {
        EntityRendererManager renderManager = MC.getRenderManager();
        matrices.push();

        double x = target.lastTickPosX + (target.getPosX() - target.lastTickPosX) * partialTicks - renderManager.info.getProjectedView().getX();
        double y = target.lastTickPosY + (target.getPosY() - target.lastTickPosY) * partialTicks - renderManager.info.getProjectedView().getY();
        double z = target.lastTickPosZ + (target.getPosZ() - target.lastTickPosZ) * partialTicks - renderManager.info.getProjectedView().getZ();
        matrices.translate(x, y, z);

        float height = target.getHeight();
        double elapsed = (System.currentTimeMillis() % (long) CIRCLE_DURATION) / CIRCLE_DURATION;
        boolean side = elapsed > 0.5;
        double progress = side ? (elapsed - 0.5) * 2.0 : elapsed * 2.0;
        progress = progress < 0.5 ? 2.0 * progress * progress : 1.0 - Math.pow(-2.0 * progress + 2.0, 2.0) / 2.0;
        double eased = (height / 2.0f) * (progress > 0.5 ? 1.0 - progress : progress) * (side ? -1.0 : 1.0);

        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        RenderSystem.disableCull();
        RenderSystem.lineWidth(1.5f);

        int baseColor = resolveTargetColor(target);
        float[] rgba = ColorUtil.toNormalized(ColorUtil.setAlpha(baseColor, Math.round(visibility * 255.0f)));

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 360; i++) {
            double radians = Math.toRadians(i);
            float radius = target.getWidth() * 0.8f;
            buffer.pos(matrices.getLast().getMatrix(),
                    (float) (Math.cos(radians) * radius),
                    (float) (height * progress),
                    (float) (Math.sin(radians) * radius)).color(rgba[0], rgba[1], rgba[2], rgba[3] * 0.5f).endVertex();
            buffer.pos(matrices.getLast().getMatrix(),
                    (float) (Math.cos(radians) * radius),
                    (float) (height * progress + eased),
                    (float) (Math.sin(radians) * radius)).color(rgba[0], rgba[1], rgba[2], 0.0f).endVertex();
        }
        buffer.finishDrawing();
        WorldVertexBufferUploader.draw(buffer);

        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 360; i++) {
            double radians = Math.toRadians(i);
            float radius = target.getWidth() * 0.8f;
            buffer.pos(matrices.getLast().getMatrix(),
                    (float) (Math.cos(radians) * radius),
                    (float) (height * progress),
                    (float) (Math.sin(radians) * radius)).color(rgba[0], rgba[1], rgba[2], rgba[3] * 0.5f).endVertex();
        }
        buffer.finishDrawing();
        WorldVertexBufferUploader.draw(buffer);

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableAlphaTest();
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
        RenderSystem.shadeModel(GL11.GL_FLAT);
        matrices.pop();
    }

    private int resolveTargetColor(Entity entity) {
        int base = TargetColorPalette.primary();
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            if (living.hurtTime > 0) {
                return ColorUtil.rgba(220, 80, 80, 255);
            }
            float hurtProgress = Math.min(living.hurtTime / 2.0f, 1.0f);
            return ColorUtil.interpolate(TargetColorPalette.secondary(), base, hurtProgress);
        }
        return base;
    }
}
