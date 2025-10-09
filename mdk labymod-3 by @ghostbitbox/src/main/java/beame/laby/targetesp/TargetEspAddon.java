package beame.laby.targetesp;

import com.google.gson.JsonObject;
import net.labymod.api.LabyModAddon;
import net.labymod.api.event.EventService;
import net.labymod.gui.elements.DropDownMenu;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.DropDownElement;
import net.labymod.settings.elements.SettingsElement;

import java.lang.reflect.Method;
import java.util.List;

public class TargetEspAddon extends LabyModAddon {

    private final TargetEspConfig config = new TargetEspConfig();
    private TargetEspController controller;
    private EventService eventService;

    @Override
    public void onEnable() {
        if (controller == null) {
            controller = new TargetEspController(this);
        }

        eventService = obtainEventService();

        if (eventService != null) {
            try {
                eventService.registerListener(controller);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        if (controller != null && eventService != null) {
            tryUnregisterListener(eventService, controller);
        }

        if (controller != null) {
            controller.shutdown();
            controller = null;
        }

        eventService = null;
    }

    @Override
    public void loadConfig() {
        JsonObject json = getConfig();
        config.load(json);
        persistConfig();
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

    private EventService obtainEventService() {
        try {
            if (getApi() != null) {
                EventService apiService = getApi().getEventService();
                if (apiService != null) {
                    return apiService;
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        try {
            return EventService.getInstance();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    private void tryUnregisterListener(EventService service, Object listener) {
        try {
            Method unregister = service.getClass().getMethod("unregisterListener", Object.class);
            unregister.invoke(service, listener);
        } catch (NoSuchMethodException ignored) {
            // Older API builds do not expose an unregister hook, so we simply skip it.
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
