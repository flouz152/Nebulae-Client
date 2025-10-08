package beame.setting;

import java.util.function.Supplier;

public class ConfigSetting<Value> implements Configurable {
// leaked by itskekoff; discord.gg/sk3d LaQolvr5

    Value defaultVal;

    String settingName;
    public Supplier<Boolean> visible = () -> true;

    public ConfigSetting(String name, Value defaultVal) {
        this.settingName = name;
        this.defaultVal = defaultVal;
    }

    public String getName() {
        return settingName;
    }

    @Override
    public ConfigSetting<?> setVisible(Supplier<Boolean> bool) {
        visible = bool;
        return this;
    }

    public void set(Value value) {
        defaultVal = value;
    }

    public Value get() {
        return defaultVal;
    }
}