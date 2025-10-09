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
    private EventService registeredEventService;

    @Override
    public void onEnable() {
        controller = new TargetEspController(this);
        EventService eventService = resolveEventService();
        registeredEventService = eventService;
        try {
            eventService.registerListener(controller);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        if (controller != null) {
            if (registeredEventService != null) {
                try {
                    registeredEventService.getClass()
                            .getMethod("unregisterListener", Object.class)
                            .invoke(registeredEventService, controller);
                } catch (NoSuchMethodException ignored) {
                    // Older API builds do not expose an unregister hook, so guard the call.
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            controller.shutdown();
            controller = null;
        }
        registeredEventService = null;
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
        JsonObject json = getConfig();
        if (json != null) {
            config.save(json);
            saveConfig();
        }
    }

    private EventService resolveEventService() {
        try {
            EventService apiService = getApi() != null ? getApi().getEventService() : null;
            if (apiService != null) {
                return apiService;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return EventService.getInstance();
    }
}
