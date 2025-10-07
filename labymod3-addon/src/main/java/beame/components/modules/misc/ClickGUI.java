package beame.components.modules.misc;

import events.Event;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.BindSetting;
import beame.setting.SettingList.BooleanSetting;

public class ClickGUI extends Module {
// leaked by itskekoff; discord.gg/sk3d nvU3OgNn

    public ClickGUI () {
        super("ClickGUI", Category.Misc, true, "Под-настройки меню клиента");
        addSettings(clickGuiBind, clickGuiHints);
    }

    public final BindSetting clickGuiBind = new BindSetting("Открыть GUI", 344);
    public final BooleanSetting clickGuiHints = new BooleanSetting("Описание модулей", true);

    @Override
    public void event(Event event) {
        if (isState()) {
            toggle();
        }
    }
}
