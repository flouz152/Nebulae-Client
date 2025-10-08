package beame.setting;

import java.util.function.Supplier;

public interface Configurable {
// leaked by itskekoff; discord.gg/sk3d ca34Cbob
    ConfigSetting<?> setVisible(Supplier<Boolean> bool);
}