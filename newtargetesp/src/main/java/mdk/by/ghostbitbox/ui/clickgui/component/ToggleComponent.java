package mdk.by.ghostbitbox.ui.clickgui.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;

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
        AbstractGui.fill(matrixStack, (int) x, (int) y, (int) (x + width), (int) (y + getHeight()), 0x331B1B26);
        FontRenderer font = font();
        if (font == null) {
            return;
        }
        boolean value = getter.get();
        font.drawString(matrixStack, label, x + 6, y + 8, 0xFFEFEFF5);
        String status = value ? "ON" : "OFF";
        int color = value ? 0xFF6CE3B6 : 0xFFEF7474;
        font.drawString(matrixStack, status, x + width - 6 - font.getStringWidth(status), y + 8, color);
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
