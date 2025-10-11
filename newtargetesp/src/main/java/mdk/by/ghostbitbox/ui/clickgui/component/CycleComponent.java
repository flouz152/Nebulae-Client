package mdk.by.ghostbitbox.ui.clickgui.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.gui.FontRenderer;
import mdk.by.ghostbitbox.util.HudRenderUtil;

public class CycleComponent<T> extends SettingComponent {

    private final String label;
    private final T[] values;
    private final Supplier<T> getter;
    private final Consumer<T> setter;
    private final Function<T, String> formatter;

    public CycleComponent(String label, T[] values, Supplier<T> getter, Consumer<T> setter, Function<T, String> formatter, Runnable saveAction) {
        super(26.0f, saveAction);
        this.label = label;
        this.values = values;
        this.getter = getter;
        this.setter = setter;
        this.formatter = formatter;
    }

    @Override
    public void render(MatrixStack matrixStack, float x, float y, float width, int mouseX, int mouseY, float partialTicks) {
        setBounds(x, y, width, true);
        FontRenderer font = font();
        if (font == null) {
            return;
        }
        HudRenderUtil.drawRoundedRect(matrixStack, x, y, width, getHeight(), 4.0f, 0x44161A22);
        font.drawString(matrixStack, label, x + 6, y + 7, 0xFFEFEFF5);
        String value = formatter.apply(getter.get());
        float chipWidth = font.getStringWidth(value) + 12.0f;
        float chipX = x + width - chipWidth - 6.0f;
        float chipY = y + (getHeight() - 14.0f) / 2.0f;
        HudRenderUtil.drawRoundedRect(matrixStack, chipX, chipY, chipWidth, 14.0f, 6.0f, 0x33252A35);
        font.drawString(matrixStack, value, chipX + (chipWidth - font.getStringWidth(value)) / 2.0f, y + 7, 0xFFCED3DA);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY) || values.length == 0) {
            return false;
        }
        int direction = button == 1 ? -1 : 1;
        T current = getter.get();
        int index = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i] == current) {
                index = i;
                break;
            }
        }
        index = (index + direction + values.length) % values.length;
        setter.accept(values[index]);
        requestSave();
        return true;
    }
}
