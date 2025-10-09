package beame.laby.targetesp;

import com.google.gson.JsonObject;
import net.labymod.api.LabyModAddon;
import net.labymod.api.event.EventService;
import net.labymod.gui.elements.DropDownMenu;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.DropDownElement;
import net.labymod.settings.elements.SettingsElement;

import java.util.List;

public class TargetEspAddon extends LabyModAddon {

    private final TargetEspConfig config = new TargetEspConfig();
    private TargetEspController controller;

    @Override
    public void onEnable() {
        controller = new TargetEspController(this);
        EventService eventService = getApi().getEventService();
        if (eventService != null) {
            eventService.registerListener(controller);
        } else {
            EventService.getInstance().registerListener(controller);
        }
    }

    @Override
    public void onDisable() {
        if (controller != null) {
            controller.shutdown();
            controller = null;
        }
    }

    @Override
    public void loadConfig() {
        JsonObject json = getConfig();
        config.load(json);
        config.save(json);
        saveConfig();
    }

    @Override
    protected void fillSettings(List<SettingsElement> settings) {
        ControlElement.IconData icon = new ControlElement.IconData();

        BooleanElement enabledSetting = new BooleanElement("Включить", icon, value -> {
            config.setEnabled(value);
            persistConfig();
        }, config.isEnabled());

        DropDownMenu<TargetEspMode> modeMenu = new DropDownMenu<>("Режим", 0, 0, 140, 16);
        modeMenu.fill(TargetEspMode.values());
        modeMenu.setSelected(config.getMode());

        DropDownElement<TargetEspMode> modeSetting = new DropDownElement<>("Режим", "", modeMenu, icon, TargetEspMode::valueOf);
        modeSetting.setChangeListener(selected -> {
            if (selected != null) {
                config.setMode(selected);
                persistConfig();
            }
        });

        settings.add(enabledSetting);
        settings.add(modeSetting);
    }

    public TargetEspConfig configuration() {
        return config;
    }

    private void persistConfig() {
        config.save(getConfig());
        saveConfig();
    }
}
