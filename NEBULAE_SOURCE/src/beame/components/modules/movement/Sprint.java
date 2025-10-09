package beame.components.modules.movement;

import events.Event;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.BooleanSetting;

public class Sprint extends Module {
// leaked by itskekoff; discord.gg/sk3d tJ1uUGP4
    public boolean canSprint;

    public final BooleanSetting keepSprint = new BooleanSetting("Сохранять спринт",true);

    public Sprint() {
        super("Sprint", Category.Movement, true, "Автоматический бег");
        addSettings(keepSprint);
    }

    @Override
    public void event(Event event) {
    }
}