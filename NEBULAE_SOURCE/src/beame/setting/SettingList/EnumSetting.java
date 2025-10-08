package beame.setting.SettingList;

import beame.setting.ConfigSetting;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class EnumSetting extends ConfigSetting<List<BooleanSetting>> {
// leaked by itskekoff; discord.gg/sk3d saEbXuMc
    public EnumSetting(String name, BooleanSetting... strings) {
        super(name, Arrays.asList(strings));
    }

    public BooleanSetting getValueByName(String settingName) {
        return get().stream().filter(booleanSetting -> booleanSetting.getName().equalsIgnoreCase(settingName)).findFirst().orElse(null);
    }

    public BooleanSetting get(String settingName) {
        return get().stream().filter(booleanSetting -> booleanSetting.getName().equalsIgnoreCase(settingName)).findFirst().orElse(null);
    }

    public BooleanSetting get(int index) {
        return get().get(index);
    }
    public boolean getB(int index) {
        return this.get().get(index).get();
    }

    @Override
    public EnumSetting setVisible(Supplier<Boolean> bool) {
        return (EnumSetting) super.setVisible(bool);
    }
}