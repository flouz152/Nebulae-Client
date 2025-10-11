package mdk.by.ghostbitbox.ui.clickgui.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;

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
        if (font != null) {
            font.drawString(matrixStack, text, x, y + 4, 0xFFD0D0D0);
        }
    }
}
