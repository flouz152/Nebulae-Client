package beame.setting.SettingList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import beame.setting.ConfigSetting;

import java.util.function.Supplier;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InputFieldSetting extends ConfigSetting<String> {
// leaked by itskekoff; discord.gg/sk3d fCIOOXoo

    final String description;
    public InputFieldSetting(String name, String defaultVal, String description) {
        super(name, defaultVal);
        this.description = description;
    }

    @Override
    public InputFieldSetting setVisible(Supplier<Boolean> bool) {
        return (InputFieldSetting) super.setVisible(bool);
    }
}