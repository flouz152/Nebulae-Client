package beame.laby.targetesp;

import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.SettingsElement;

import java.util.List;

public class TargetEspAddon extends LabyModAddon {

    private TargetEspConfig config;
    private TargetEspController controller;

    @Override
    public void onEnable() {
        controller = new TargetEspController(this);
        getApi().registerForgeListener(controller);
    }

    @Override
    public void onDisable() {
        if (controller != null) {
            getApi().unregisterForgeListener(controller);
            controller.reset();
            controller = null;
        }
    }

    @Override
    public void loadConfig() {
        configuration().load();
    }

    @Override
    protected void fillSettings(List<SettingsElement> settings) {
        configuration().fillSettings(settings);
    }

    public TargetEspConfig configuration() {
        if (config == null) {
            config = new TargetEspConfig(this);
        }
        return config;
    }
}
