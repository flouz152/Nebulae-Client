package mdk.by.ghostbitbox.ui.clickgui.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.math.MathHelper;

public class SliderComponent extends SettingComponent {

    private final String label;
    private final float min;
    private final float max;
    private final float step;
    private final Supplier<Float> getter;
    private final Consumer<Float> setter;
    private final ValueFormatter formatter;
    private boolean dragging;
    private float sliderLeft;
    private float sliderWidth;
    private float value;

    public SliderComponent(String label, float min, float max, float step, Supplier<Float> getter, Consumer<Float> setter, ValueFormatter formatter, Runnable saveAction) {
        super(34.0f, saveAction);
        this.label = label;
        this.min = min;
        this.max = max;
        this.step = step;
        this.getter = getter;
        this.setter = setter;
        this.formatter = formatter;
        this.value = getter.get();
    }

    @Override
    public void render(MatrixStack matrixStack, float x, float y, float width, int mouseX, int mouseY, float partialTicks) {
        setBounds(x, y, width, true);
        if (!dragging) {
            value = getter.get();
        }
        AbstractGui.fill(matrixStack, (int) x, (int) y, (int) (x + width), (int) (y + getHeight()), 0x331B1B26);
        FontRenderer font = font();
        if (font == null) {
            return;
        }
        font.drawString(matrixStack, label, x + 6, y + 6, 0xFFEFEFF5);
        String text = formatter.format(value);
        font.drawString(matrixStack, text, x + width - 6 - font.getStringWidth(text), y + 6, 0xFF9AA0A8);

        sliderLeft = x + 8;
        sliderWidth = width - 16;
        float sliderTop = y + getHeight() - 12;
        float sliderBottom = sliderTop + 6;
        AbstractGui.fill(matrixStack, (int) sliderLeft, (int) sliderTop, (int) (sliderLeft + sliderWidth), (int) sliderBottom, 0x5520202A);
        float percentage = (value - min) / (max - min);
        float filledWidth = sliderWidth * MathHelper.clamp(percentage, 0.0f, 1.0f);
        AbstractGui.fill(matrixStack, (int) sliderLeft, (int) sliderTop, (int) (sliderLeft + filledWidth), (int) sliderBottom, 0xFF4C84FF);
        int knobX = (int) (sliderLeft + filledWidth - 3);
        AbstractGui.fill(matrixStack, knobX, (int) sliderTop - 2, knobX + 6, (int) sliderBottom + 2, 0xFFCBD9FF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isMouseOver(mouseX, mouseY)) {
            dragging = true;
            updateValue(mouseX);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging) {
            updateValue(mouseX);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging && button == 0) {
            dragging = false;
            requestSave();
            return true;
        }
        return false;
    }

    private void updateValue(double mouseX) {
        if (sliderWidth <= 0.0f) {
            return;
        }
        float ratio = (float) ((mouseX - sliderLeft) / sliderWidth);
        ratio = MathHelper.clamp(ratio, 0.0f, 1.0f);
        float raw = min + ratio * (max - min);
        if (step > 0.0f) {
            raw = Math.round(raw / step) * step;
        }
        raw = MathHelper.clamp(raw, min, max);
        value = raw;
        setter.accept(raw);
    }

    @FunctionalInterface
    public interface ValueFormatter {
        String format(float value);
    }
}
