package beame.components.modules.misc;

import events.Event;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.EnumSetting;
import beame.setting.SettingList.SliderSetting;

public class ClientSounds extends Module {
// leaked by itskekoff; discord.gg/sk3d ZOam8AvW
    public ClientSounds() {
        super("ClientSounds", Category.Misc, true, "Звуки клиента");
        addSettings(volume, soundActive);
    }

    public final SliderSetting volume = new SliderSetting("Громкость", 80, 50, 100, 5);

    public final EnumSetting soundActive = new EnumSetting("Элементы проигрования",
            new BooleanSetting("При вкл/выкл модуля", false),
            new BooleanSetting("При вводе в текстовое поле", false),
            new BooleanSetting("При наведении на модуль", false),
            new BooleanSetting("При сохранении конфигурации", false),
            new BooleanSetting("При загрузке конфигурации", false),
            new BooleanSetting("При обнаружении администратора", false),
            new BooleanSetting("При добавлении в друзья", false),
            new BooleanSetting("При обычном уведомлении", false),
            new BooleanSetting("При закрытии/открытии настроек модуля", false)
    );

    @Override
    public void event(Event event) { }
}
