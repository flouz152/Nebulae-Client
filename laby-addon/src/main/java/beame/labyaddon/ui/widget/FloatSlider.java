package beame.labyaddon.ui.widget;

import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Slider widget that operates on floating point ranges with configurable precision.
 */
public class FloatSlider extends AbstractSlider {

    private final String label;
    private final Supplier<Float> getter;
    private final Consumer<Float> setter;
    private final float min;
    private final float max;
    private final float step;

    public FloatSlider(int x, int y, int width, int height, String label,
                       Supplier<Float> getter, Consumer<Float> setter,
                       float min, float max, float step) {
        super(x, y, width, height, StringTextComponent.EMPTY, 0.0D);
        this.label = label;
        this.getter = getter;
        this.setter = setter;
        this.min = min;
        this.max = max;
        this.step = step;
        this.sliderValue = normalize(snap(getter.get()));
        updateMessage();
    }

    @Override
    protected void func_230979_b_() {
        updateMessage();
    }

    @Override
    protected void func_230972_a_() {
        setter.accept(snap(denormalize(this.sliderValue)));
    }

    private void updateMessage() {
        float value = snap(denormalize(this.sliderValue));
        this.setMessage(new StringTextComponent(String.format("%s: %.2f", label, value)));
    }

    private double normalize(float value) {
        return MathHelper.clamp((value - min) / (max - min), 0.0F, 1.0F);
    }

    private float denormalize(double value) {
        return min + (float) value * (max - min);
    }

    private float snap(float value) {
        float clamped = MathHelper.clamp(value, min, max);
        return Math.round(clamped / step) * step;
    }
}
