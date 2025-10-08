package net.optifine.render;

import beame.util.color.ColorUtils;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderTypeBuffers;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;

public class RenderUtils
{
// leaked by itskekoff; discord.gg/sk3d jN0p2xrr
    private static boolean flushRenderBuffers = true;
    private static Minecraft mc = Minecraft.getInstance();
    static Tessellator TESSELLATOR = Tessellator.getInstance();
    static BufferBuilder BUILDER = TESSELLATOR.getBuffer();

    public static boolean setFlushRenderBuffers(boolean flushRenderBuffers)
    {
        boolean flag = RenderUtils.flushRenderBuffers;
        RenderUtils.flushRenderBuffers = flushRenderBuffers;
        return flag;
    }

    public static boolean isFlushRenderBuffers()
    {
        return flushRenderBuffers;
    }

    public static void flushRenderBuffers()
    {
        if (flushRenderBuffers)
        {
            RenderTypeBuffers rendertypebuffers = mc.getRenderTypeBuffers();
            rendertypebuffers.getBufferSource().flushRenderBuffers();
            rendertypebuffers.getCrumblingBufferSource().flushRenderBuffers();
        }
    }


    public static void drawBlockBox(BlockPos blockPos, int color) {
        double x = blockPos.getX(),
                y = blockPos.getY(),
                z = blockPos.getZ();
        GL11.glPushMatrix();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(1);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        color(color);
        drawBox(new AxisAlignedBB(x, y, z, x + 1, y + 1.0, z + 1));
        drawBox(new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1));
        GL11.glLineWidth(2);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        resetColor();
        GL11.glPopMatrix();
    }

    public static void drawBox(AxisAlignedBB boundingBox) {
        BUILDER.begin(3, DefaultVertexFormats.POSITION);
        BUILDER.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        BUILDER.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        BUILDER.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        BUILDER.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        BUILDER.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        TESSELLATOR.draw();
        BUILDER.begin(3, DefaultVertexFormats.POSITION);
        BUILDER.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        BUILDER.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        BUILDER.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        BUILDER.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        BUILDER.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        TESSELLATOR.draw();
        BUILDER.begin(1, DefaultVertexFormats.POSITION);
        BUILDER.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        BUILDER.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        BUILDER.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        BUILDER.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        BUILDER.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        BUILDER.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        BUILDER.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        BUILDER.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        TESSELLATOR.draw();
    }

    public static void color(int color) {
        color(color, (float) (color >> 24 & 255) / 255.0F);
    }
    public static void color(int color, float alpha) {
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        GlStateManager.color4f(r, g, b, alpha);
    }
    public static void resetColor() {
        GlStateManager.color4f(1, 1, 1, 1);
    }

}
