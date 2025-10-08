package beame.setting.SettingList;

import beame.setting.ConfigSetting;

import java.util.function.Supplier;

public class SliderSetting extends ConfigSetting<Float> {
// leaked by itskekoff; discord.gg/sk3d NdfqWunY

    public float min;
    public float max;
    public float increment;

    public SliderSetting(String name, float defaultVal, float min, float max, float increment) {
        super(name, defaultVal);
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    @Override
    public SliderSetting setVisible(Supplier<Boolean> bool) {
        return (SliderSetting) super.setVisible(bool);
    }
}