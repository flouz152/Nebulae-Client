package beame.setting.SettingList;

import beame.setting.ConfigSetting;

import java.util.function.Supplier;

public class BindSetting extends ConfigSetting<Integer> {
// leaked by itskekoff; discord.gg/sk3d cGUypMqh
    public BindSetting(String name, Integer defaultVal) {
        super(name, defaultVal);
    }

    @Override
    public BindSetting setVisible(Supplier<Boolean> bool) {
        return (BindSetting) super.setVisible(bool);
    }
}
