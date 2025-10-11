package mdk.by.ghostbitbox.ui.clickgui.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import mdk.by.ghostbitbox.util.HudRenderUtil;

public class HeadingComponent extends SettingComponent {

    private final String text;

    public HeadingComponent(String text) {
        super(18.0f, null);
        this.text = text;
    }

    @Override
    public void render(MatrixStack matrixStack, float x, float y, float width, int mouseX, int mouseY, float partialTicks) {
        setBounds(x, y, width, true);
        FontRenderer font = font();
        if (font == null) {
            return;
        }
        font.drawString(matrixStack, text, x, y + 4, 0xFFE0E3EA);
        HudRenderUtil.drawRoundedRect(matrixStack, x, y + getHeight() - 3.0f, width, 2.0f, 1.0f,
                0x22FFFFFF);
        HudRenderUtil.drawRoundedRect(matrixStack, x, y + getHeight() - 3.0f, width * 0.35f, 2.0f, 1.0f,
                themeColor());
    }
}
