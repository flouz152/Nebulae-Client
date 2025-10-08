package dev.nebulae.targetesp;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.nebulae.targetesp.util.ColorHelper;
import dev.nebulae.targetesp.util.ProjectionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class TargetEspRenderer {

    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation("targetesp", "textures/glow.png");
    private static final ResourceLocation QUAD_TEXTURE = new ResourceLocation("targetesp", "textures/quad.png");
    private static final ResourceLocation QUAD_NEW_TEXTURE = new ResourceLocation("targetesp", "textures/quad_new.png");

    private final Minecraft minecraft = Minecraft.getInstance();
    private final TargetEspAddon addon;
    private long startTime = System.currentTimeMillis();

    public TargetEspRenderer(TargetEspAddon addon) {
        this.addon = addon;
    }

    public void resetAnimation() {
        this.startTime = System.currentTimeMillis();
    }

    public void renderWorld(RenderWorldLastEvent event, LivingEntity target, float alpha) {
        TargetEspMode mode = this.addon.configuration().modeProperty().get();
        if (mode == TargetEspMode.GHOSTS) {
            renderGhosts(event.getMatrixStack(), event.getPartialTicks(), target, alpha);
        } else if (mode == TargetEspMode.CIRCLE) {
            renderCircle(event.getMatrixStack(), event.getPartialTicks(), target, alpha);
        }
    }

    public void renderOverlay(RenderGameOverlayEvent.Post event, LivingEntity target, float alpha) {
        TargetEspMode mode = this.addon.configuration().modeProperty().get();
        ResourceLocation texture = mode == TargetEspMode.SQUARE ? QUAD_TEXTURE : QUAD_NEW_TEXTURE;
        drawOverlayQuad(event.getMatrixStack(), event.getPartialTicks(), target, texture, alpha);
    }

    private void renderGhosts(MatrixStack stack, float partialTicks, LivingEntity target, float alpha) {
        EntityRendererManager renderManager = this.minecraft.getRenderManager();
        ActiveRenderInfo camera = renderManager.info;
        if (camera == null) {
            return;
        }

        Vector3d projectedView = camera.getProjectedView();
        double x = MathHelper.lerp(partialTicks, target.lastTickPosX, target.getPosX()) - projectedView.getX();
        double y = MathHelper.lerp(partialTicks, target.lastTickPosY, target.getPosY()) - projectedView.getY();
        double z = MathHelper.lerp(partialTicks, target.lastTickPosZ, target.getPosZ()) - projectedView.getZ();

        stack.push();
        RenderSystem.pushMatrix();

        RenderSystem.disableLighting();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        RenderSystem.disableCull();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO, GL11.GL_ONE);

        stack.translate(x + 0.20000000298023224D, y + target.getHeight() / 4.0F + 0.75D, z);
        this.minecraft.getTextureManager().bindTexture(GLOW_TEXTURE);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        double radius = 0.699999988079071D;
        float speed = this.addon.configuration().ghostSpeedProperty().get().floatValue();
        float size = this.addon.configuration().ghostWidthProperty().get().floatValue();
        double distance = 12.0D;
        int length = this.addon.configuration().ghostLengthProperty().get().intValue();
        float angleStep = this.addon.configuration().ghostAngleProperty().get().floatValue();

        Quaternion cameraRotation = camera.getRotation().copy();
        int baseColor = resolveBaseColor(target, alpha);
        long time = System.currentTimeMillis();

        for (int axis = 0; axis < 3; axis++) {
            for (int i = 0; i < length; ++i) {
                Quaternion rotation = cameraRotation.copy();
                buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);

                double angle = angleStep * ((time - this.startTime) - (double) i * distance) / (double) speed;
                double sin = Math.sin(angle) * radius;
                double cos = Math.cos(angle) * radius;

                applyAxisTranslation(stack, axis, sin, cos);
                stack.translate(-size / 2.0F, -size / 2.0F, 0.0D);
                stack.rotate(rotation);
                stack.translate(size / 2.0F, size / 2.0F, 0.0D);

                Matrix4f matrix = stack.getLast().getMatrix();
                int alphaStep = (int) (alpha * 255.0F * (1.0F - (float) i / (float) length));
                int faded = ColorHelper.withAlpha(baseColor, alphaStep);
                int[] comps = ColorHelper.toComponents(faded);

                buffer.pos(matrix, 0.0F, -size, 0.0F).color(comps[0], comps[1], comps[2], comps[3]).tex(0.0F, 0.0F).endVertex();
                buffer.pos(matrix, -size, -size, 0.0F).color(comps[0], comps[1], comps[2], comps[3]).tex(0.0F, 1.0F).endVertex();
                buffer.pos(matrix, -size, 0.0F, 0.0F).color(comps[0], comps[1], comps[2], comps[3]).tex(1.0F, 1.0F).endVertex();
                buffer.pos(matrix, 0.0F, 0.0F, 0.0F).color(comps[0], comps[1], comps[2], comps[3]).tex(1.0F, 0.0F).endVertex();
                tessellator.draw();

                stack.translate(-size / 2.0F, -size / 2.0F, 0.0D);
                rotation.conjugate();
                stack.rotate(rotation);
                stack.translate(size / 2.0F, size / 2.0F, 0.0D);
                revertAxisTranslation(stack, axis, sin, cos);
            }
        }

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.enableAlphaTest();
        RenderSystem.depthMask(true);
        RenderSystem.popMatrix();
        stack.pop();
    }

    private void renderCircle(MatrixStack stack, float partialTicks, LivingEntity target, float alpha) {
        EntityRendererManager renderManager = this.minecraft.getRenderManager();
        ActiveRenderInfo camera = renderManager.info;
        if (camera == null) {
            return;
        }

        Vector3d projectedView = camera.getProjectedView();
        double x = MathHelper.lerp(partialTicks, target.lastTickPosX, target.getPosX()) - projectedView.getX();
        double y = MathHelper.lerp(partialTicks, target.lastTickPosY, target.getPosY()) - projectedView.getY();
        double z = MathHelper.lerp(partialTicks, target.lastTickPosZ, target.getPosZ()) - projectedView.getZ();

        stack.push();
        stack.translate(x, y, z);

        double duration = this.addon.configuration().circleSpeedProperty().get();
        double elapsed = System.currentTimeMillis() % duration;
        boolean reverse = elapsed > duration / 2.0D;
        double progress = elapsed / (duration / 2.0D);
        progress = reverse ? (progress - 1.0D) : 1.0D - progress;
        progress = progress < 0.5D ? 2.0D * progress * progress : 1.0D - Math.pow(-2.0D * progress + 2.0D, 2.0D) / 2.0D;
        double eased = target.getHeight() / 2.0D * (progress > 0.5D ? 1.0D - progress : progress) * (reverse ? -1.0D : 1.0D);

        int baseColor = resolveBaseColor(target, alpha);
        int[] solid = ColorHelper.toComponents(ColorHelper.withAlpha(baseColor, (int) (alpha * 180.0F)));
        int[] transparent = ColorHelper.toComponents(ColorHelper.withAlpha(baseColor, 0));

        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        RenderSystem.disableCull();
        RenderSystem.lineWidth(1.5F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 360; ++i) {
            double rad = Math.toRadians(i);
            float px = (float) (Math.cos(rad) * (double) target.getWidth() * 0.8D);
            float pz = (float) (Math.sin(rad) * (double) target.getWidth() * 0.8D);

            buffer.pos(stack.getLast().getMatrix(), px, (float) (target.getHeight() * progress), pz)
                    .color(solid[0], solid[1], solid[2], solid[3]).endVertex();
            buffer.pos(stack.getLast().getMatrix(), px, (float) (target.getHeight() * progress + eased), pz)
                    .color(transparent[0], transparent[1], transparent[2], transparent[3]).endVertex();
        }
        tessellator.draw();

        buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 360; ++i) {
            double rad = Math.toRadians(i);
            float px = (float) (Math.cos(rad) * (double) target.getWidth() * 0.8D);
            float pz = (float) (Math.sin(rad) * (double) target.getWidth() * 0.8D);
            buffer.pos(stack.getLast().getMatrix(), px, (float) (target.getHeight() * progress), pz)
                    .color(solid[0], solid[1], solid[2], solid[3]).endVertex();
        }
        tessellator.draw();

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        RenderSystem.enableAlphaTest();
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        RenderSystem.shadeModel(GL11.GL_FLAT);

        stack.pop();
    }

    private void drawOverlayQuad(MatrixStack stack, float partialTicks, LivingEntity target, ResourceLocation texture, float alpha) {
        Vector2f screen = ProjectionUtil.projectTo2D(target, partialTicks, target.getHeight() / 2.0D);
        if (screen == null) {
            return;
        }

        float size = this.minecraft.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON ? 90.0F : 60.0F;
        float rotation = (float) (Math.sin(System.currentTimeMillis() / 1000.0D) * 120.0D);
        int color = ColorHelper.withAlpha(resolveBaseColor(target, alpha), (int) (alpha * 255.0F));
        int[] comps = ColorHelper.toComponents(color);

        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        this.minecraft.getTextureManager().bindTexture(texture);

        stack.push();
        stack.translate(screen.x, screen.y, 0.0D);
        stack.rotate(Vector3f.ZP.rotationDegrees(rotation));
        stack.translate(-screen.x, -screen.y, 0.0D);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        Matrix4f matrix = stack.getLast().getMatrix();

        float left = screen.x - size / 2.0F;
        float top = screen.y - size / 2.0F;
        float right = screen.x + size / 2.0F;
        float bottom = screen.y + size / 2.0F;

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
        buffer.pos(matrix, left, top, 0.0F).color(comps[0], comps[1], comps[2], comps[3]).tex(0.0F, 0.0F).endVertex();
        buffer.pos(matrix, left, bottom, 0.0F).color(comps[0], comps[1], comps[2], comps[3]).tex(0.0F, 1.0F).endVertex();
        buffer.pos(matrix, right, bottom, 0.0F).color(comps[0], comps[1], comps[2], comps[3]).tex(1.0F, 1.0F).endVertex();
        buffer.pos(matrix, right, top, 0.0F).color(comps[0], comps[1], comps[2], comps[3]).tex(1.0F, 0.0F).endVertex();
        tessellator.draw();

        stack.pop();

        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
    }

    private int resolveBaseColor(LivingEntity target, float alpha) {
        int dynamic = Color.HSBtoRGB((System.currentTimeMillis() % 4500L) / 4500.0F, 0.65F, 1.0F);
        dynamic = 0xFF000000 | (dynamic & 0xFFFFFF);

        if (this.addon.configuration().redOnHurtProperty().get() && target.hurtTime > 0) {
            int hurtColor = ColorHelper.fromRGBA(220, 80, 80, 255);
            float progress = ColorHelper.clamp(target.hurtTime / 10.0F, 0.0F, 1.0F);
            return ColorHelper.lerp(hurtColor, hurtColor, progress);
        }

        float hurtProgress = ColorHelper.clamp(target.hurtTime / 10.0F, 0.0F, 1.0F);
        int calmColor = ColorHelper.lerp(dynamic, ColorHelper.fromRGBA(70, 200, 255, 255), 0.35F);
        int hurtTint = ColorHelper.fromRGBA(150, 90, 255, 255);
        return ColorHelper.lerp(calmColor, hurtTint, hurtProgress);
    }

    private void applyAxisTranslation(MatrixStack stack, int axis, double sin, double cos) {
        switch (axis) {
            case 0 -> stack.translate(sin, cos, -cos);
            case 1 -> stack.translate(-sin, sin, -cos);
            case 2 -> stack.translate(cos, -sin, -sin);
            default -> {
            }
        }
    }

    private void revertAxisTranslation(MatrixStack stack, int axis, double sin, double cos) {
        switch (axis) {
            case 0 -> stack.translate(-sin, -cos, cos);
            case 1 -> stack.translate(sin, -sin, cos);
            case 2 -> stack.translate(-cos, sin, sin);
            default -> {
            }
        }
    }
}
