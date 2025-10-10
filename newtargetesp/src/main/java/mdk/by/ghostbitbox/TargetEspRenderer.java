package mdk.by.ghostbitbox;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mdk.by.ghostbitbox.util.ColorUtil;
import mdk.by.ghostbitbox.util.HudRenderUtil;
import mdk.by.ghostbitbox.util.MathUtil;
import mdk.by.ghostbitbox.util.ProjectionUtil;
import mdk.by.ghostbitbox.util.TargetColorPalette;
import mdk.by.ghostbitbox.util.animation.AnimationMath;
import mdk.by.ghostbitbox.util.animation.DecelerateAnimation;
import mdk.by.ghostbitbox.util.animation.Direction;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import org.joml.Vector2d;
import org.lwjgl.opengl.GL11;

public class TargetEspRenderer {

    private static final Minecraft MC = Minecraft.getInstance();

    private static final ResourceLocation GLOW = new ResourceLocation("night/image/glow.png");
    private static final ResourceLocation QUAD = new ResourceLocation("night/image/target/Quad.png");
    private static final ResourceLocation QUAD_NEW = new ResourceLocation("night/image/target/Quad2.png");

    private static final float GHOST_SPEED = 33.0f;
    private static final int GHOST_LENGTH = 24;
    private static final float GHOST_WIDTH = 0.4f;
    private static final double GHOST_RADIUS = 0.699999988079071d;
    private static final double GHOST_DISTANCE = 12.0d;
    private static final float GHOST_ANGLE_STEP = 0.18f;
    private static final double CIRCLE_DURATION = 2000.0d;

    private static final long START_TIME = System.currentTimeMillis();

    private final Tessellator tessellator = Tessellator.getInstance();
    private final BufferBuilder buffer = tessellator.getBuffer();

    private final DecelerateAnimation alpha = new DecelerateAnimation(600, 255.0d);
    private float alphaState;

    public void updateState(boolean hasTarget) {
        alphaState = MathHelper.clamp(AnimationMath.fast(alphaState, hasTarget ? 1.0f : 0.0f, 8.0f), 0.0f, 1.0f);
        alpha.setDirection(hasTarget ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    public void drawHud(MatrixStack stack, Entity target, TargetEspMode mode, float visibility, float partialTicks) {
        if (target == null || target == MC.player) {
            return;
        }
        if (mode != TargetEspMode.SQUARE && mode != TargetEspMode.NEW_SQUARE) {
            return;
        }

        draw2DSoulsMarker(stack, target, mode, visibility, partialTicks);
    }

    public void drawWorld(MatrixStack stack, Entity target, TargetEspMode mode, float visibility, float partialTicks) {
        if (target == null || target == MC.player) {
            return;
        }

        if (mode == TargetEspMode.GHOSTS) {
            drawGhosts(stack, target, visibility, partialTicks);
        } else if (mode == TargetEspMode.CIRCLE) {
            drawCircle(stack, target, visibility, partialTicks);
        }
    }

    private void draw2DSoulsMarker(MatrixStack stack, Entity target, TargetEspMode mode, float visibility, float partialTicks) {
        int alphaOutput = Math.min(255, (int) (alpha.getOutput() * MathHelper.clamp(visibility, 0.0f, 1.0f)));
        if (alphaOutput <= 0) {
            return;
        }

        Vector3d interpolated = MathUtil.interpolate(target.getPositionVec(),
                new Vector3d(target.lastTickPosX, target.lastTickPosY, target.lastTickPosZ), partialTicks);
        Vector2d screen = ProjectionUtil.toScreen(interpolated.x, interpolated.y + target.getHeight() / 2.0f, interpolated.z);
        if (screen == null) {
            return;
        }

        float size = MC.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON ? 90.0f : 60.0f;
        Vector2f pos = new Vector2f((float) screen.x, (float) screen.y);
        float rotation = (float) (Math.sin(System.currentTimeMillis() / 1000.0d) * 120.0d);
        ResourceLocation texture = mode == TargetEspMode.SQUARE ? QUAD : QUAD_NEW;
        int color = ColorUtil.setAlpha(resolveTargetColor(target), alphaOutput);

        stack.push();
        stack.translate(pos.x, pos.y, 0.0f);
        stack.rotate(Vector3f.ZP.rotationDegrees(rotation));
        stack.translate(-pos.x, -pos.y, 0.0f);
        HudRenderUtil.drawImage(stack, texture, pos.x - size / 2.0f, pos.y - size / 2.0f, size, size, color, 0.0f);
        stack.pop();
    }

    private void drawGhosts(MatrixStack stack, Entity target, float visibility, float partialTicks) {
        if (visibility <= 0.0f) {
            return;
        }

        stack.push();
        RenderSystem.pushMatrix();
        RenderSystem.disableLighting();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        RenderSystem.disableCull();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, 0, 1);

        ActiveRenderInfo camera = MC.getRenderManager().info;
        stack.translate(-camera.getProjectedView().getX(), -camera.getProjectedView().getY(), -camera.getProjectedView().getZ());

        Vector3d interpolated = MathUtil.interpolate(target.getPositionVec(),
                new Vector3d(target.lastTickPosX, target.lastTickPosY, target.lastTickPosZ), partialTicks);
        interpolated = interpolated.add(0.20000000298023224d, target.getHeight() / 4.0f, 0.0d);
        stack.translate(interpolated.x, interpolated.y, interpolated.z);

        MC.getTextureManager().bindTexture(GLOW);

        int baseColor = resolveTargetColor(target);
        float visibilityFactor = MathHelper.clamp(visibility, 0.0f, 1.0f);

        for (int i = 0; i < GHOST_LENGTH; ++i) {
            double angle = computeGhostAngle(i);
            double sin = Math.sin(angle) * GHOST_RADIUS;
            double cos = Math.cos(angle) * GHOST_RADIUS;
            renderGhost(stack, camera, baseColor, visibilityFactor, i, sin, cos, -cos);
        }

        for (int i = 0; i < GHOST_LENGTH; ++i) {
            double angle = computeGhostAngle(i);
            double sin = Math.sin(angle) * GHOST_RADIUS;
            double cos = Math.cos(angle) * GHOST_RADIUS;
            renderGhost(stack, camera, baseColor, visibilityFactor, i, -sin, sin, -cos);
        }

        for (int i = 0; i < GHOST_LENGTH; ++i) {
            double angle = computeGhostAngle(i);
            double sin = Math.sin(angle) * GHOST_RADIUS;
            double cos = Math.cos(angle) * GHOST_RADIUS;
            renderGhost(stack, camera, baseColor, visibilityFactor, i, cos, -sin, -sin);
        }

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.enableAlphaTest();
        RenderSystem.depthMask(true);
        RenderSystem.popMatrix();
        stack.pop();
    }

    private double computeGhostAngle(int index) {
        double elapsed = System.currentTimeMillis() - START_TIME;
        return GHOST_ANGLE_STEP * (elapsed - index * GHOST_DISTANCE) / GHOST_SPEED;
    }

    private void renderGhost(MatrixStack stack, ActiveRenderInfo camera, int baseColor, float visibilityFactor, int index,
                              double translateX, double translateY, double translateZ) {
        Quaternion rotation = camera.getRotation().copy();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);

        stack.translate(translateX, translateY, translateZ);
        stack.translate(-GHOST_WIDTH / 2.0f, -GHOST_WIDTH / 2.0f, 0.0d);
        stack.rotate(rotation);
        stack.translate(GHOST_WIDTH / 2.0f, GHOST_WIDTH / 2.0f, 0.0d);

        int alphaValue = Math.min(255, (int) (alphaState * visibilityFactor * (index * 10.0f)));
        int tinted = ColorUtil.setAlpha(baseColor, alphaValue);
        float[] rgba = ColorUtil.toNormalized(tinted);
        Matrix4f matrix = stack.getLast().getMatrix();

        buffer.pos(matrix, 0.0f, -GHOST_WIDTH, 0.0f).color(rgba[0], rgba[1], rgba[2], rgba[3]).tex(0.0f, 0.0f).endVertex();
        buffer.pos(matrix, -GHOST_WIDTH, -GHOST_WIDTH, 0.0f).color(rgba[0], rgba[1], rgba[2], rgba[3]).tex(0.0f, 1.0f).endVertex();
        buffer.pos(matrix, -GHOST_WIDTH, 0.0f, 0.0f).color(rgba[0], rgba[1], rgba[2], rgba[3]).tex(1.0f, 1.0f).endVertex();
        buffer.pos(matrix, 0.0f, 0.0f, 0.0f).color(rgba[0], rgba[1], rgba[2], rgba[3]).tex(1.0f, 0.0f).endVertex();
        tessellator.draw();

        stack.translate(-GHOST_WIDTH / 2.0f, -GHOST_WIDTH / 2.0f, 0.0d);
        rotation.conjugate();
        stack.rotate(rotation);
        stack.translate(GHOST_WIDTH / 2.0f, GHOST_WIDTH / 2.0f, 0.0d);
        stack.translate(-translateX, -translateY, -translateZ);
    }

    private void drawCircle(MatrixStack stack, Entity target, float visibility, float partialTicks) {
        if (visibility <= 0.0f) {
            return;
        }

        EntityRendererManager renderManager = MC.getRenderManager();
        stack.push();

        double x = target.lastTickPosX + (target.getPosX() - target.lastTickPosX) * partialTicks - renderManager.info.getProjectedView().getX();
        double y = target.lastTickPosY + (target.getPosY() - target.lastTickPosY) * partialTicks - renderManager.info.getProjectedView().getY();
        double z = target.lastTickPosZ + (target.getPosZ() - target.lastTickPosZ) * partialTicks - renderManager.info.getProjectedView().getZ();
        stack.translate(x, y, z);

        float height = target.getHeight();
        double elapsed = System.currentTimeMillis() % CIRCLE_DURATION;
        double halfDuration = CIRCLE_DURATION / 2.0d;
        boolean side = elapsed > halfDuration;
        double progress = elapsed / halfDuration;
        progress = side ? progress - 1.0d : 1.0d - progress;
        progress = progress < 0.5d ? 2.0d * progress * progress : 1.0d - Math.pow(-2.0d * progress + 2.0d, 2.0d) / 2.0d;
        double eased = (height / 2.0f) * (progress > 0.5d ? 1.0d - progress : progress) * (side ? -1.0d : 1.0d);

        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        RenderSystem.disableCull();
        RenderSystem.lineWidth(1.5f);
        RenderSystem.color4f(-1.0f, -1.0f, -1.0f, -1.0f);

        int baseColor = resolveTargetColor(target);
        float[] rgba = ColorUtil.toNormalized(baseColor);
        float alphaFactor = MathHelper.clamp(visibility, 0.0f, 1.0f);
        float radius = target.getWidth() * 0.8f;
        Matrix4f matrix = stack.getLast().getMatrix();

        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 360; ++i) {
            double radians = Math.toRadians(i);
            float offsetX = (float) (Math.cos(radians) * radius);
            float offsetZ = (float) (Math.sin(radians) * radius);
            buffer.pos(matrix, offsetX, (float) (height * progress), offsetZ)
                    .color(rgba[0], rgba[1], rgba[2], rgba[3] * 0.5f * alphaFactor).endVertex();
            buffer.pos(matrix, offsetX, (float) (height * progress + eased), offsetZ)
                    .color(rgba[0], rgba[1], rgba[2], 0.0f).endVertex();
        }
        buffer.finishDrawing();
        WorldVertexBufferUploader.draw(buffer);

        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 360; ++i) {
            double radians = Math.toRadians(i);
            float offsetX = (float) (Math.cos(radians) * radius);
            float offsetZ = (float) (Math.sin(radians) * radius);
            buffer.pos(matrix, offsetX, (float) (height * progress), offsetZ)
                    .color(rgba[0], rgba[1], rgba[2], rgba[3] * 0.5f * alphaFactor).endVertex();
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
        stack.pop();
    }

    private int resolveTargetColor(Entity entity) {
        int primary = TargetColorPalette.primary();
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            if (living.hurtTime > 0) {
                return ColorUtil.rgba(220, 80, 80, 255);
            }
            float hurtProgress = Math.min(living.hurtTime / 2.0f, 1.0f);
            return ColorUtil.interpolate(TargetColorPalette.secondary(), primary, hurtProgress);
        }
        return primary;
    }
}
