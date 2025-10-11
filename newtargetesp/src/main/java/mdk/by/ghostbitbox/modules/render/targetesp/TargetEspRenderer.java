package mdk.by.ghostbitbox.modules.render.targetesp;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mdk.by.ghostbitbox.TargetEspConfig;
import mdk.by.ghostbitbox.util.ColorUtil;
import mdk.by.ghostbitbox.util.MathUtil;
import mdk.by.ghostbitbox.util.ProjectionUtil;
import mdk.by.ghostbitbox.util.TargetEspTextures;
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
import net.minecraft.util.math.vector.Vector3d;
import org.joml.Vector2d;
import org.lwjgl.opengl.GL11;

public class TargetEspRenderer {

    private static final Minecraft MC = Minecraft.getInstance();
    private static final Tessellator TESSELLATOR = Tessellator.getInstance();
    private static final BufferBuilder BUFFER = TESSELLATOR.getBuffer();
    private static final long START_TIME = System.currentTimeMillis();

    private final DecelerateAnimation alpha = new DecelerateAnimation(600, 255.0d);
    private float alphaState;

    private int baseColor = ColorUtil.rgba(120, 190, 255, 255);
    private int hurtColor = ColorUtil.rgba(220, 80, 80, 255);
    private boolean hurtTint = true;
    private float ghostSpeed = 33.0f;
    private int ghostLength = 24;
    private float ghostWidth = 0.4f;
    private float ghostAngleStep = 0.18f;
    private float ghostRadius = 0.7f;
    private float ghostSpacing = 12.0f;
    private float ghostHeightOffset = 0.0f;
    private double circleDuration = 2000.0d;
    private float circleRadiusMultiplier = 0.8f;
    private float hudSizeFirstPerson = 90.0f;
    private float hudSizeThirdPerson = 60.0f;

    public void applyConfiguration(TargetEspConfig config) {
        if (config == null) {
            return;
        }

        baseColor = config.getBaseColor();
        hurtColor = config.getHurtColor();
        hurtTint = config.isHurtTintEnabled();
        ghostSpeed = Math.max(0.1f, config.getGhostSpeed());
        ghostLength = Math.max(1, config.getGhostLength());
        ghostWidth = Math.max(0.05f, config.getGhostWidth());
        ghostAngleStep = Math.max(0.01f, config.getGhostAngle());
        ghostRadius = Math.max(0.1f, config.getGhostRadius());
        ghostSpacing = Math.max(1.0f, config.getGhostSpacing());
        ghostHeightOffset = MathHelper.clamp(config.getGhostHeightOffset(), -3.0f, 3.0f);
        circleDuration = Math.max(100.0d, config.getCircleDuration());
        circleRadiusMultiplier = Math.max(0.1f, config.getCircleRadius());
        hudSizeFirstPerson = Math.max(10.0f, config.getHudSizeFirstPerson());
        hudSizeThirdPerson = Math.max(10.0f, config.getHudSizeThirdPerson());
    }

    public void updateState(boolean hasTarget) {
        alphaState = MathHelper.clamp(AnimationMath.fast(alphaState, hasTarget ? 1.0f : 0.0f, 8.0f), 0.0f, 1.0f);
        alpha.setDirection(hasTarget ? Direction.FORWARDS : Direction.BACKWARDS);
    }

    public void drawHud(MatrixStack stack, Entity target, TargetEspMode mode, float visibility, float partialTicks) {
        if (target == null || target == MC.player) {
            return;
        }

        if (mode == TargetEspMode.SQUARE) {
            drawSquareOverlay(stack, target, TargetEspTextures.getSquareTexture(), visibility, partialTicks);
        } else if (mode == TargetEspMode.NEW_SQUARE) {
            drawSquareOverlay(stack, target, TargetEspTextures.getNewSquareTexture(), visibility, partialTicks);
        }
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

    private void drawSquareOverlay(MatrixStack stack, Entity target, ResourceLocation texture, float visibility, float partialTicks) {
        Vector3d interpolated = MathUtil.interpolate(target.getPositionVec(),
                new Vector3d(target.lastTickPosX, target.lastTickPosY, target.lastTickPosZ), partialTicks);
        Vector2d screen = ProjectionUtil.toScreen(interpolated.x,
                interpolated.y + target.getHeight() / 2.0f, interpolated.z);
        if (screen == null) {
            return;
        }

        float size = MC.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON ? hudSizeFirstPerson : hudSizeThirdPerson;
        int alphaValue = MathHelper.clamp((int) (alpha.getOutput() * MathHelper.clamp(visibility, 0.0f, 1.0f)), 0, 255);
        if (alphaValue <= 0) {
            return;
        }

        int color = ColorUtil.setAlpha(resolveTargetColor(target), alphaValue);
        float centerX = (float) screen.x;
        float centerY = (float) screen.y;
        float half = size / 2.0f;
        float rotation = (float) (Math.sin(System.currentTimeMillis() / 1000.0d) * 120.0d);

        RenderSystem.pushMatrix();
        RenderSystem.disableLighting();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        RenderSystem.disableCull();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, 0, 1);

        RenderSystem.translatef(centerX, centerY, 0.0f);
        RenderSystem.rotatef(rotation, 0.0f, 0.0f, 1.0f);
        RenderSystem.translatef(-centerX, -centerY, 0.0f);

        MC.getTextureManager().bindTexture(texture);
        Matrix4f matrix = stack.getLast().getMatrix();
        float left = centerX - half;
        float top = centerY - half;
        float right = centerX + half;
        float bottom = centerY + half;
        float[] rgba = ColorUtil.toNormalized(color);

        BUFFER.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        BUFFER.pos(matrix, left, top, 0.0f).tex(0.0f, 0.0f).color(rgba[0], rgba[1], rgba[2], rgba[3]).endVertex();
        BUFFER.pos(matrix, left, bottom, 0.0f).tex(0.0f, 1.0f).color(rgba[0], rgba[1], rgba[2], rgba[3]).endVertex();
        BUFFER.pos(matrix, right, bottom, 0.0f).tex(1.0f, 1.0f).color(rgba[0], rgba[1], rgba[2], rgba[3]).endVertex();
        BUFFER.pos(matrix, right, top, 0.0f).tex(1.0f, 0.0f).color(rgba[0], rgba[1], rgba[2], rgba[3]).endVertex();
        BUFFER.finishDrawing();
        WorldVertexBufferUploader.draw(BUFFER);

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.enableAlphaTest();
        RenderSystem.depthMask(true);
        RenderSystem.popMatrix();
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
        Vector3d projected = camera.getProjectedView();
        stack.translate(-projected.getX(), -projected.getY(), -projected.getZ());

        Vector3d interpolated = MathUtil.interpolate(target.getPositionVec(),
                new Vector3d(target.lastTickPosX, target.lastTickPosY, target.lastTickPosZ), partialTicks);
        interpolated = interpolated.add(0.20000000298023224d, target.getHeight() / 4.0f + ghostHeightOffset, 0.0d);
        stack.translate(interpolated.x, interpolated.y, interpolated.z);

        MC.getTextureManager().bindTexture(TargetEspTextures.getGlowTexture());
        int color = resolveTargetColor(target);

        for (int i = 0; i < ghostLength; ++i) {
            double angle = computeGhostAngle(i);
            renderGhostColumn(stack, camera, color, i, visibility,
                    Math.sin(angle) * ghostRadius, Math.cos(angle) * ghostRadius, -Math.cos(angle) * ghostRadius);
        }
        for (int i = 0; i < ghostLength; ++i) {
            double angle = computeGhostAngle(i);
            renderGhostColumn(stack, camera, color, i, visibility,
                    -Math.sin(angle) * ghostRadius, Math.sin(angle) * ghostRadius, -Math.cos(angle) * ghostRadius);
        }
        for (int i = 0; i < ghostLength; ++i) {
            double angle = computeGhostAngle(i);
            renderGhostColumn(stack, camera, color, i, visibility,
                    Math.cos(angle) * ghostRadius, -Math.sin(angle) * ghostRadius, -Math.sin(angle) * ghostRadius);
        }

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.enableAlphaTest();
        RenderSystem.depthMask(true);
        RenderSystem.popMatrix();
        stack.pop();
    }

    private void renderGhostColumn(MatrixStack stack, ActiveRenderInfo camera, int baseColor, int index, float visibility,
                                   double translateX, double translateY, double translateZ) {
        Quaternion rotation = camera.getRotation().copy();
        BUFFER.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);

        stack.translate(translateX, translateY, translateZ);
        stack.translate(-ghostWidth / 2.0f, -ghostWidth / 2.0f, 0.0d);
        stack.rotate(rotation);
        stack.translate(ghostWidth / 2.0f, ghostWidth / 2.0f, 0.0d);

        int alphaValue = MathHelper.clamp((int) (alphaState * (index * 10.0f) * visibility), 0, 255);
        float[] rgba = ColorUtil.toNormalized(ColorUtil.setAlpha(baseColor, alphaValue));
        Matrix4f matrix = stack.getLast().getMatrix();

        BUFFER.pos(matrix, 0.0f, -ghostWidth, 0.0f).tex(0.0f, 0.0f).color(rgba[0], rgba[1], rgba[2], rgba[3]).endVertex();
        BUFFER.pos(matrix, -ghostWidth, -ghostWidth, 0.0f).tex(0.0f, 1.0f).color(rgba[0], rgba[1], rgba[2], rgba[3]).endVertex();
        BUFFER.pos(matrix, -ghostWidth, 0.0f, 0.0f).tex(1.0f, 1.0f).color(rgba[0], rgba[1], rgba[2], rgba[3]).endVertex();
        BUFFER.pos(matrix, 0.0f, 0.0f, 0.0f).tex(1.0f, 0.0f).color(rgba[0], rgba[1], rgba[2], rgba[3]).endVertex();
        BUFFER.finishDrawing();
        WorldVertexBufferUploader.draw(BUFFER);

        stack.translate(-ghostWidth / 2.0f, -ghostWidth / 2.0f, 0.0d);
        rotation.conjugate();
        stack.rotate(rotation);
        stack.translate(ghostWidth / 2.0f, ghostWidth / 2.0f, 0.0d);
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

        double elapsed = System.currentTimeMillis() % circleDuration;
        double halfDuration = circleDuration / 2.0d;
        boolean side = elapsed > halfDuration;
        double progress = elapsed / halfDuration;
        progress = side ? progress - 1.0d : 1.0d - progress;
        progress = progress < 0.5d ? 2.0d * progress * progress : 1.0d - Math.pow(-2.0d * progress + 2.0d, 2.0d) / 2.0d;
        double eased = (target.getHeight() / 2.0d) * (progress > 0.5d ? 1.0d - progress : progress) * (side ? -1.0d : 1.0d);

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        RenderSystem.disableCull();
        RenderSystem.lineWidth(1.5f);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

        float[] rgba = ColorUtil.toNormalized(resolveTargetColor(target));
        float alphaFactor = MathHelper.clamp(visibility, 0.0f, 1.0f);
        double radius = Math.max(0.1d, target.getWidth() * circleRadiusMultiplier);
        Matrix4f matrix = stack.getLast().getMatrix();

        BUFFER.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 360; ++i) {
            double radians = Math.toRadians(i);
            float offsetX = (float) (Math.cos(radians) * radius);
            float offsetZ = (float) (Math.sin(radians) * radius);
            float baseY = (float) (target.getHeight() * progress);
            BUFFER.pos(matrix, offsetX, baseY, offsetZ)
                    .color(rgba[0], rgba[1], rgba[2], rgba[3] * 0.5f * alphaFactor).endVertex();
            BUFFER.pos(matrix, offsetX, baseY + (float) eased, offsetZ)
                    .color(rgba[0], rgba[1], rgba[2], 0.0f).endVertex();
        }
        BUFFER.finishDrawing();
        WorldVertexBufferUploader.draw(BUFFER);

        BUFFER.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 360; ++i) {
            double radians = Math.toRadians(i);
            float offsetX = (float) (Math.cos(radians) * radius);
            float offsetZ = (float) (Math.sin(radians) * radius);
            float baseY = (float) (target.getHeight() * progress);
            BUFFER.pos(matrix, offsetX, baseY, offsetZ)
                    .color(rgba[0], rgba[1], rgba[2], rgba[3] * 0.5f * alphaFactor).endVertex();
        }
        BUFFER.finishDrawing();
        WorldVertexBufferUploader.draw(BUFFER);

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableAlphaTest();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDepthMask(true);
        RenderSystem.shadeModel(GL11.GL_FLAT);
        stack.pop();
    }

    private double computeGhostAngle(int index) {
        double elapsed = System.currentTimeMillis() - START_TIME;
        return ghostAngleStep * (elapsed - index * ghostSpacing) / ghostSpeed;
    }

    private int resolveTargetColor(Entity entity) {
        if (hurtTint && entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            if (living.hurtTime > 0) {
                float progress = Math.min(living.hurtTime / 2.0f, 1.0f);
                return ColorUtil.interpolate(hurtColor, hurtColor, progress);
            }
        }
        return baseColor;
    }
}
