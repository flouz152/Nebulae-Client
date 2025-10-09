package beame.laby.targetesp;

import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.config.setting.DropdownSetting;
import net.labymod.api.client.gui.screen.widget.widgets.config.setting.SwitchSetting;
import net.labymod.api.configuration.loader.property.ConfigProperty;

public class TargetEspConfig extends AddonConfig {

    @SwitchSetting
    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

    @DropdownSetting
    private final ConfigProperty<TargetEspMode> mode = new ConfigProperty<>(TargetEspMode.GHOSTS);

    public ConfigProperty<TargetEspMode> mode() {
        return mode;
    }

    @Override
    public ConfigProperty<Boolean> enabled() {
        return enabled;
    }
}
