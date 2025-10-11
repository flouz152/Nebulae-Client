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

    public static void drawImage(MatrixStack stack, ResourceLocation texture, float x, float y, float width, float height, int color, float rotation, boolean additive) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return;
        }

        RenderSystem.pushMatrix();
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

        Matrix4f matrix = stack.getLast().getMatrix();

        RenderSystem.translatef(x + width / 2.0f, y + height / 2.0f, 0.0f);
        RenderSystem.rotatef(rotation, 0.0f, 0.0f, 1.0f);
        RenderSystem.translatef(-(x + width / 2.0f), -(y + height / 2.0f), 0.0f);

        minecraft.getTextureManager().bindTexture(texture);

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        float a = ((color >> 24) & 0xFF) / 255.0f;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

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
        RenderSystem.popMatrix();
    }

}
