package beame.labyaddon.util.render;

import beame.labyaddon.util.IMinecraft;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;

import java.awt.Color;

public final class ClientHandler implements IMinecraft {

    private ClientHandler() {
    }

    public static void drawImage(MatrixStack stack, ResourceLocation texture, float x, float y, float width, float height, int color, float rotation) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.translatef(x + width / 2.0F, y + height / 2.0F, 0.0F);
        RenderSystem.rotatef(rotation, 0.0F, 0.0F, 1.0F);
        RenderSystem.translatef(-(x + width / 2.0F), -(y + height / 2.0F), 0.0F);
        mc.getTextureManager().bindTexture(texture);
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        RenderSystem.color4f(r / 255.0F, g / 255.0F, b / 255.0F, a / 255.0F);
        AbstractGui.blit(stack, (int) x, (int) y, 0, 0, (int) width, (int) height, (int) width, (int) height);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    public static void drawImage(MatrixStack stack, ResourceLocation texture, float x, float y, float width, float height, Color color, float rotation) {
        drawImage(stack, texture, x, y, width, height, color.getRGB(), rotation);
    }
}
