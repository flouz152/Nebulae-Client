package beame.setting.SettingList;


import beame.setting.ConfigSetting;

import java.util.function.Supplier;

public class RadioSetting extends ConfigSetting<String> {
// leaked by itskekoff; discord.gg/sk3d C0F4Gpqx

    public String[] strings;

    public RadioSetting(String name, String defaultVal, String... strings) {
        super(name, defaultVal);
        this.strings = strings;
    }

    public int getIndex() {
        int index = 0;
        for (String val : strings) {
            if (val.equalsIgnoreCase(get())) {
                return index;
            }
            index++;
        }
        return 0;
    }

    public boolean is(String s) {
        return get().equalsIgnoreCase(s);
    }
    public boolean get(String s) {
        return get().equalsIgnoreCase(s);
    }
    @Override
    public RadioSetting setVisible(Supplier<Boolean> bool) {
        return (RadioSetting) super.setVisible(bool);
    }

}