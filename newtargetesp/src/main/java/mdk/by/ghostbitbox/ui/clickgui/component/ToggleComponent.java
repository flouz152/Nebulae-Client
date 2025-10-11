package mdk.by.ghostbitbox.ui.clickgui.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.FontRenderer;
import mdk.by.ghostbitbox.util.ColorUtil;
import mdk.by.ghostbitbox.util.HudRenderUtil;

public class ToggleComponent extends SettingComponent {

    private final String label;
    private final Supplier<Boolean> getter;
    private final Consumer<Boolean> setter;

    public ToggleComponent(String label, Supplier<Boolean> getter, Consumer<Boolean> setter, Runnable saveAction) {
        super(26.0f, saveAction);
        this.label = label;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public void render(MatrixStack matrixStack, float x, float y, float width, int mouseX, int mouseY, float partialTicks) {
        setBounds(x, y, width, true);
        FontRenderer font = font();
        if (font == null) {
            return;
        }
        boolean value = getter.get();
        HudRenderUtil.drawRoundedRect(matrixStack, x, y, width, getHeight(), 4.0f, 0x44161A22);
        font.drawString(matrixStack, label, x + 6, y + 7, 0xFFEFEFF5);

        float toggleWidth = 26.0f;
        float toggleHeight = 12.0f;
        float toggleX = x + width - toggleWidth - 8.0f;
        float toggleY = y + (getHeight() - toggleHeight) / 2.0f;
        int trackColor = value ? ColorUtil.setAlpha(themeColor(), 160) : 0xFF2C2F3A;
        HudRenderUtil.drawRoundedRect(matrixStack, toggleX, toggleY, toggleWidth, toggleHeight, 6.0f, trackColor);

        float knobSize = toggleHeight - 2.0f;
        float knobX = value ? toggleX + toggleWidth - knobSize - 1.0f : toggleX + 1.0f;
        int knobColor = value ? themeColor() : 0xFF6B6E78;
        HudRenderUtil.drawRoundedRect(matrixStack, knobX, toggleY + 1.0f, knobSize, knobSize, knobSize / 2.0f, knobColor);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isMouseOver(mouseX, mouseY)) {
            setter.accept(!getter.get());
            requestSave();
            return true;
        }
        return false;
    }
}
