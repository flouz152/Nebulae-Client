package beame.setting.SettingList;

import lombok.Getter;
import lombok.Setter;
import beame.setting.ConfigSetting;

import java.util.function.Supplier;

public class BooleanSetting extends ConfigSetting<Boolean> {
// leaked by itskekoff; discord.gg/sk3d 7CZfvgv5
    public enum Mode {
        HOLD,
        TOGGLED
    }

    public float alpha = 0;
    public float textScroll = 0;

    @Getter
    private Mode currentMode;
    @Getter
    @Setter
    private Integer bindVal;

    public BooleanSetting(String name, Boolean defaultVal, Integer bindVal) {
        super(name, defaultVal);
        this.currentMode = Mode.TOGGLED;
        this.bindVal = bindVal;
    }

    public BooleanSetting(String name) {
        super(name, false);
        this.currentMode = Mode.TOGGLED;
        this.bindVal = 0;
    }

    public BooleanSetting(String name, Boolean defaultVal) {
        super(name, defaultVal);
        this.currentMode = Mode.TOGGLED;
        this.bindVal = 0;
    }

    public void setMode(Mode mode) {
        this.currentMode = mode;
    }

    public Mode getMode() {
        return currentMode;
    }

    @Override
    public BooleanSetting setVisible(Supplier<Boolean> bool) {
        return (BooleanSetting) super.setVisible(bool);
    }


}
