package mdk.by.ghostbitbox.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import org.lwjgl.opengl.GL11;

public final class HudRenderUtil {

    private HudRenderUtil() {
    }

    public static void drawImage(MatrixStack matrices, ResourceLocation texture, float x, float y, float width, float height, int color, boolean additive) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return;
        }

        mc.getTextureManager().bindTexture(texture);

        float a = ((color >> 24) & 0xFF) / 255.0f;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        RenderSystem.disableLighting();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        RenderSystem.disableCull();
        RenderSystem.disableAlphaTest();
        if (additive) {
            RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE, 0, 1);
        } else {
            RenderSystem.defaultBlendFunc();
        }

        Matrix4f matrix = matrices.getLast().getMatrix();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(matrix, x, y, 0.0f).tex(0.0f, 0.0f).color(r, g, b, a).endVertex();
        buffer.pos(matrix, x, y + height, 0.0f).tex(0.0f, 1.0f).color(r, g, b, a).endVertex();
        buffer.pos(matrix, x + width, y + height, 0.0f).tex(1.0f, 1.0f).color(r, g, b, a).endVertex();
        buffer.pos(matrix, x + width, y, 0.0f).tex(1.0f, 0.0f).color(r, g, b, a).endVertex();
        buffer.finishDrawing();
        WorldVertexBufferUploader.draw(buffer);

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.enableAlphaTest();
        RenderSystem.depthMask(true);
        RenderSystem.shadeModel(GL11.GL_FLAT);
    }

    public static void drawImage(MatrixStack matrices, ResourceLocation texture, float x, float y, float width, float height,
                                  int color, float rotationDegrees, boolean additive) {
        matrices.push();
        matrices.translate(x + width / 2.0f, y + height / 2.0f, 0.0f);
        matrices.rotate(Vector3f.ZP.rotationDegrees(rotationDegrees));
        matrices.translate(-(x + width / 2.0f), -(y + height / 2.0f), 0.0f);
        drawImage(matrices, texture, x, y, width, height, color, additive);
        matrices.pop();
    }

    public static void drawRoundedRect(MatrixStack matrices, float x, float y, float width, float height, float radius, int color) {
        if (radius <= 0.0f) {
            fill(matrices, x, y, width, height, color);
            return;
        }

        float effectiveRadius = Math.min(radius, Math.min(width / 2.0f, height / 2.0f));
        float centerX = x + effectiveRadius;
        float centerY = y + effectiveRadius;
        float right = x + width - effectiveRadius;
        float bottom = y + height - effectiveRadius;

        fill(matrices, centerX, y, width - effectiveRadius * 2.0f, height, color);
        fill(matrices, x, centerY, effectiveRadius, height - effectiveRadius * 2.0f, color);
        fill(matrices, right, centerY, effectiveRadius, height - effectiveRadius * 2.0f, color);

        drawCorner(matrices, centerX, centerY, effectiveRadius, 180, 270, color);
        drawCorner(matrices, right, centerY, effectiveRadius, 270, 360, color);
        drawCorner(matrices, right, bottom, effectiveRadius, 0, 90, color);
        drawCorner(matrices, centerX, bottom, effectiveRadius, 90, 180, color);
    }

    public static void drawBorderedRoundedRect(MatrixStack matrices, float x, float y, float width, float height, float radius,
                                               int fillColor, int borderColor, float borderWidth) {
        if (borderWidth <= 0.0f) {
            drawRoundedRect(matrices, x, y, width, height, radius, fillColor);
            return;
        }

        drawRoundedRect(matrices, x, y, width, height, radius, borderColor);
        float inset = borderWidth;
        drawRoundedRect(matrices, x + inset, y + inset, width - inset * 2.0f, height - inset * 2.0f,
                Math.max(0.0f, radius - inset), fillColor);
    }

    public static void drawHorizontalGradient(MatrixStack matrices, float x, float y, float width, float height,
                                              int leftColor, int rightColor) {
        float leftA = ((leftColor >> 24) & 0xFF) / 255.0f;
        if (leftA <= 0.0f && ((rightColor >> 24) & 0xFF) <= 0) {
            return;
        }

        float leftR = ((leftColor >> 16) & 0xFF) / 255.0f;
        float leftG = ((leftColor >> 8) & 0xFF) / 255.0f;
        float leftB = (leftColor & 0xFF) / 255.0f;

        float rightR = ((rightColor >> 16) & 0xFF) / 255.0f;
        float rightG = ((rightColor >> 8) & 0xFF) / 255.0f;
        float rightB = (rightColor & 0xFF) / 255.0f;
        float rightA = ((rightColor >> 24) & 0xFF) / 255.0f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();

        Matrix4f matrix = matrices.getLast().getMatrix();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(matrix, x + width, y, 0.0f).color(rightR, rightG, rightB, rightA).endVertex();
        buffer.pos(matrix, x, y, 0.0f).color(leftR, leftG, leftB, leftA).endVertex();
        buffer.pos(matrix, x, y + height, 0.0f).color(leftR, leftG, leftB, leftA).endVertex();
        buffer.pos(matrix, x + width, y + height, 0.0f).color(rightR, rightG, rightB, rightA).endVertex();
        buffer.finishDrawing();
        WorldVertexBufferUploader.draw(buffer);

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void fill(MatrixStack matrices, float x, float y, float width, float height, int color) {
        float a = ((color >> 24) & 0xFF) / 255.0f;
        if (a <= 0.0f) {
            return;
        }

        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();

        Matrix4f matrix = matrices.getLast().getMatrix();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(matrix, x, y + height, 0.0f).color(r, g, b, a).endVertex();
        buffer.pos(matrix, x + width, y + height, 0.0f).color(r, g, b, a).endVertex();
        buffer.pos(matrix, x + width, y, 0.0f).color(r, g, b, a).endVertex();
        buffer.pos(matrix, x, y, 0.0f).color(r, g, b, a).endVertex();
        buffer.finishDrawing();
        WorldVertexBufferUploader.draw(buffer);

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    private static void drawCorner(MatrixStack matrices, float centerX, float centerY, float radius,
                                   int startAngle, int endAngle, int color) {
        if (radius <= 0.0f) {
            return;
        }

        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = ((color >> 24) & 0xFF) / 255.0f;
        if (a <= 0.0f) {
            return;
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();

        Matrix4f matrix = matrices.getLast().getMatrix();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(matrix, centerX, centerY, 0.0f).color(r, g, b, a).endVertex();
        for (int angle = startAngle; angle <= endAngle; angle += 4) {
            double rad = Math.toRadians(angle);
            float x = centerX + (float) Math.cos(rad) * radius;
            float y = centerY + (float) Math.sin(rad) * radius;
            buffer.pos(matrix, x, y, 0.0f).color(r, g, b, a).endVertex();
        }
        buffer.finishDrawing();
        WorldVertexBufferUploader.draw(buffer);

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
