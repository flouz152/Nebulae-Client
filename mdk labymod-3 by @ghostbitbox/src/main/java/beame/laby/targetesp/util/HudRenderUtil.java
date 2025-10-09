package beame.laby.targetesp.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public final class HudRenderUtil {

    private HudRenderUtil() {
    }

    public static void drawImage(MatrixStack matrices, ResourceLocation texture, float x, float y, float width, float height, int color, float zLevel) {
        Minecraft mc = Minecraft.getInstance();
        mc.getTextureManager().bindTexture(texture);

        float a = ((color >> 24) & 0xFF) / 255.0f;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        MatrixStack.Entry entry = matrices.getLast();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
        buffer.pos(entry.getMatrix(), x, y + height, zLevel).color(r, g, b, a).tex(0.0f, 1.0f).endVertex();
        buffer.pos(entry.getMatrix(), x + width, y + height, zLevel).color(r, g, b, a).tex(1.0f, 1.0f).endVertex();
        buffer.pos(entry.getMatrix(), x + width, y, zLevel).color(r, g, b, a).tex(1.0f, 0.0f).endVertex();
        buffer.pos(entry.getMatrix(), x, y, zLevel).color(r, g, b, a).tex(0.0f, 0.0f).endVertex();
        buffer.finishDrawing();
        WorldVertexBufferUploader.draw(buffer);
    }
}
