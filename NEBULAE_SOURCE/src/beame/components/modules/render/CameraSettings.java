package beame.components.modules.render;

import events.Event;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.SliderSetting;

public class CameraSettings extends Module {
// leaked by itskekoff; discord.gg/sk3d bUqgtZE2
    public CameraSettings() {
        super("CameraSettings", Category.Visuals, true, "Настройки камеры игрока от 3-го лица");
        addSettings(distance, blockclip, fortniteMode);
    }

    public final SliderSetting distance = new SliderSetting("Дистанция", 4.0F, 2.0F, 10.0F, 0.1F);
    public final BooleanSetting blockclip = new BooleanSetting("Сквозь блоки", false, 0);
    public final BooleanSetting fortniteMode = new BooleanSetting("Модификационное 3-е лицо", false, 0);

    @Override
    public void event(Event event) {

    }
}
