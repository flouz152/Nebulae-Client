package beame.components.modules.misc;

import beame.Essence;
import events.Event;
import beame.module.Category;
import beame.module.Module;

public class Tester extends Module {
// leaked by itskekoff; discord.gg/sk3d tyLFYJJ2
    public Tester() { super("Test", Category.Misc); }

    @Override
    public void event(Event event) {
        Essence.getHandler().styler.Style = Essence.getHandler().styler.Style == 1 ? 0 : 1;
        this.toggle();
    }
}
