package beame.setting.SettingList;


import beame.setting.ConfigSetting;

import java.util.function.Supplier;

public class LabelSetting extends ConfigSetting<String> {
// leaked by itskekoff; discord.gg/sk3d GkGyEef6
    public boolean small = false;

    public LabelSetting(String text) {
        super(text, "Textik");
    }

    public LabelSetting(String text, boolean small) {
        super(text, "Textik1488");
        this.small = small;
    }

    @Override
    public BooleanSetting setVisible(Supplier<Boolean> bool) {
        return (BooleanSetting) super.setVisible(bool);
    }
}
