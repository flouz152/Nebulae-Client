package mdk.by.ghostbitbox.modules.render.targetesp;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mdk.by.ghostbitbox.TargetEspConfig;
import mdk.by.ghostbitbox.util.ColorUtil;
import mdk.by.ghostbitbox.util.HudRenderUtil;
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
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.joml.Vector2d;
import org.lwjgl.opengl.GL11;

public class TargetEspRenderer {

    private static final Minecraft MC = Minecraft.getInstance();

    private static final long START_TIME = System.currentTimeMillis();

    private final Tessellator tessellator = Tessellator.getInstance();
    private final BufferBuilder buffer = tessellator.getBuffer();

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

    public void updateState(boolean hasTarget) {
        alphaState = MathHelper.clamp(AnimationMath.fast(alphaState, hasTarget ? 1.0f : 0.0f, 8.0f), 0.0f, 1.0f);
        alpha.setDirection(hasTarget ? Direction.FORWARDS : Direction.BACKWARDS);
    }

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

        float size = MC.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON ? hudSizeFirstPerson : hudSizeThirdPerson;
        Vector2f pos = new Vector2f((float) screen.x, (float) screen.y);
        float rotation = (float) (Math.sin(System.currentTimeMillis() / 1000.0d) * 120.0d);
        ResourceLocation texture = mode == TargetEspMode.SQUARE ? TargetEspTextures.getSquareTexture()
                : TargetEspTextures.getNewSquareTexture();
        int color = ColorUtil.setAlpha(resolveTargetColor(target), alphaOutput);

        float left = pos.x - size / 2.0f;
        float top = pos.y - size / 2.0f;
        HudRenderUtil.drawImage(stack, texture, left, top, size, size, color, rotation, true);
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
        double verticalOffset = target.getHeight() / 4.0f + ghostHeightOffset;
        interpolated = interpolated.add(0.20000000298023224d, verticalOffset, 0.0d);
        stack.translate(interpolated.x, interpolated.y, interpolated.z);

        MC.getTextureManager().bindTexture(TargetEspTextures.getGlowTexture());

        int baseColor = resolveTargetColor(target);
        float visibilityFactor = MathHelper.clamp(visibility, 0.0f, 1.0f);
        int length = Math.max(1, ghostLength);
        float alphaStep = 255.0f / length;

        for (int i = 0; i < length; ++i) {
            double angle = computeGhostAngle(i);
            double sin = Math.sin(angle) * ghostRadius;
            double cos = Math.cos(angle) * ghostRadius;
            renderGhost(stack, camera, baseColor, visibilityFactor, i, sin, cos, -cos, ghostWidth, alphaStep);
        }

        for (int i = 0; i < length; ++i) {
            double angle = computeGhostAngle(i);
            double sin = Math.sin(angle) * ghostRadius;
            double cos = Math.cos(angle) * ghostRadius;
            renderGhost(stack, camera, baseColor, visibilityFactor, i, -sin, sin, -cos, ghostWidth, alphaStep);
        }

        for (int i = 0; i < length; ++i) {
            double angle = computeGhostAngle(i);
            double sin = Math.sin(angle) * ghostRadius;
            double cos = Math.cos(angle) * ghostRadius;
            renderGhost(stack, camera, baseColor, visibilityFactor, i, cos, -sin, -sin, ghostWidth, alphaStep);
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
        return ghostAngleStep * (elapsed - index * ghostSpacing) / ghostSpeed;
    }

    private void renderGhost(MatrixStack stack, ActiveRenderInfo camera, int baseColor, float visibilityFactor, int index,
                              double translateX, double translateY, double translateZ, float quadSize, float alphaStep) {
        Quaternion rotation = camera.getRotation().copy();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);

        stack.translate(translateX, translateY, translateZ);
        stack.translate(-quadSize / 2.0f, -quadSize / 2.0f, 0.0d);
        stack.rotate(rotation);
        stack.translate(quadSize / 2.0f, quadSize / 2.0f, 0.0d);

        int alphaValue = Math.min(255, Math.round(alphaState * visibilityFactor * alphaStep * (index + 1)));
        int tinted = ColorUtil.setAlpha(baseColor, alphaValue);
        float[] rgba = ColorUtil.toNormalized(tinted);
        Matrix4f matrix = stack.getLast().getMatrix();

        buffer.pos(matrix, 0.0f, -quadSize, 0.0f).color(rgba[0], rgba[1], rgba[2], rgba[3]).tex(0.0f, 0.0f).endVertex();
        buffer.pos(matrix, -quadSize, -quadSize, 0.0f).color(rgba[0], rgba[1], rgba[2], rgba[3]).tex(0.0f, 1.0f).endVertex();
        buffer.pos(matrix, -quadSize, 0.0f, 0.0f).color(rgba[0], rgba[1], rgba[2], rgba[3]).tex(1.0f, 1.0f).endVertex();
        buffer.pos(matrix, 0.0f, 0.0f, 0.0f).color(rgba[0], rgba[1], rgba[2], rgba[3]).tex(1.0f, 0.0f).endVertex();
        tessellator.draw();

        stack.translate(-quadSize / 2.0f, -quadSize / 2.0f, 0.0d);
        rotation.conjugate();
        stack.rotate(rotation);
        stack.translate(quadSize / 2.0f, quadSize / 2.0f, 0.0d);
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
        double elapsed = System.currentTimeMillis() % circleDuration;
        double halfDuration = circleDuration / 2.0d;
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
        float radius = target.getWidth() * circleRadiusMultiplier;
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
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            if (hurtTint && living.hurtTime > 0) {
                float hurtProgress = MathHelper.clamp(living.hurtTime / 10.0f, 0.0f, 1.0f);
                return ColorUtil.interpolate(hurtColor, baseColor, 1.0f - hurtProgress);
            }
        }
        return baseColor;
    }
}
