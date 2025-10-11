package mdk.by.ghostbitbox;

import java.util.List;
import mdk.by.ghostbitbox.modules.render.targetesp.TargetEspController;
import mdk.by.ghostbitbox.modules.render.targetesp.TargetEspMode;
import net.labymod.api.LabyModAddon;
import net.labymod.api.event.EventService;
import net.labymod.settings.elements.SettingsElement;

public class TargetEspAddon extends LabyModAddon {

    private final TargetEspConfig configuration = new TargetEspConfig();
    private final TargetEspController controller = new TargetEspController(this);
    private boolean eventsRegistered;

    @Override
    public void onEnable() {
        if (eventsRegistered) {
            return;
        }

        EventService eventService = EventService.getInstance();
        if (eventService != null) {
            eventService.registerListener(controller);
            eventsRegistered = true;
        }
    }

    @Override
    public void onDisable() {
        controller.shutdown();
    }

    @Override
    public void loadConfig() {
        configuration.load(getConfig());
        persistConfiguration();
    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {
        // All configuration is handled inside the custom click GUI.
    }

    public TargetEspConfig configuration() {
        return configuration;
    }

    public TargetEspController controller() {
        return controller;
    }

    public void setMode(TargetEspMode mode) {
        configuration.setMode(mode);
    }

    public void setEnabled(boolean enabled) {
        configuration.setEnabled(enabled);
        if (!enabled) {
            controller.shutdown();
        }
    }

    public void persistConfiguration() {
        com.google.gson.JsonObject json = getConfig();
        if (json != null) {
            configuration.save(json);
        }
        saveConfig();
    }
}
