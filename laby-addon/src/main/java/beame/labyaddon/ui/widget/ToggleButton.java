package beame.labyaddon.ui.widget;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Simple toggle button that updates its label to reflect the current boolean state.
 */
public class ToggleButton extends Button {

    private final String label;
    private final BooleanSupplier getter;
    private final Consumer<Boolean> setter;

    public ToggleButton(int x, int y, int width, int height, String label,
                        BooleanSupplier getter, Consumer<Boolean> setter) {
        super(x, y, width, height, StringTextComponent.EMPTY, button -> {});
        this.label = label;
        this.getter = getter;
        this.setter = setter;
        updateMessage();
    }

    @Override
    public void onPress() {
        boolean newState = !getter.getAsBoolean();
        setter.accept(newState);
        updateMessage();
    }

    private void updateMessage() {
        this.setMessage(new StringTextComponent(String.format("%s: %s", label, getter.getAsBoolean() ? "Вкл" : "Выкл")));
    }
}
