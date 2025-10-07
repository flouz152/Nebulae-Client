package beame.labyaddon.ui.widget;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Button that cycles through a finite list of string values.
 */
public class CycleButton extends Button {

    private final String label;
    private final Supplier<String> getter;
    private final Consumer<String> setter;
    private final Supplier<List<String>> values;

    public CycleButton(int x, int y, int width, int height, String label,
                       Supplier<String> getter, Consumer<String> setter, Supplier<List<String>> values) {
        super(x, y, width, height, StringTextComponent.EMPTY, button -> {});
        this.label = label;
        this.getter = getter;
        this.setter = setter;
        this.values = values;
        updateMessage();
    }

    @Override
    public void onPress() {
        List<String> options = values.get();
        if (options.isEmpty()) {
            return;
        }
        String current = getter.get();
        int index = options.indexOf(current);
        int nextIndex = (index + 1) % options.size();
        setter.accept(options.get(nextIndex));
        updateMessage();
    }

    private void updateMessage() {
        this.setMessage(new StringTextComponent(String.format("%s: %s", label, getter.get())));
    }
}
