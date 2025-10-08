package beame.components.modules.render;

import events.Event;
import beame.module.Category;
import beame.module.Module;

import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.InputFieldSetting;

public class NameProtect extends Module {
// leaked by itskekoff; discord.gg/sk3d UYDf3Km2
    public NameProtect() {
        super("NameProtect", Category.Visuals, true, "Заменяет реальный никнейм");
        addSettings(nameInput, friends);
    }

    public final InputFieldSetting nameInput = new InputFieldSetting("Никнейм", "chipsina", "Введите текст...");
    public final BooleanSetting friends = new BooleanSetting("Скрывать друзей",true);

    @Override
    public void event(Event event) { }
}
