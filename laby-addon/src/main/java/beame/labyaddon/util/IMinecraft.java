package beame.labyaddon.util;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;

/**
 * Minimal Minecraft context shared across the ported modules.
 */
public interface IMinecraft {

    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder buffer = tessellator.getBuffer();
    Minecraft mc = Minecraft.getInstance();
    MainWindow window = mc.getMainWindow();
    FontRenderer fontRenderer = mc.fontRenderer;
}
