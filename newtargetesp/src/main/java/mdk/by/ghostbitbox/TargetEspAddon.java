package mdk.by.ghostbitbox;

import java.util.List;
import net.labymod.api.LabyModAddon;
import net.labymod.api.event.EventService;
import net.labymod.gui.elements.DropDownMenu;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement.IconData;
import net.labymod.settings.elements.DropDownElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Material;

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
        DropDownMenu<TargetEspMode> menu = new DropDownMenu<>("Highlight mode", 0, 0, 0, 0);
        menu.fill(TargetEspMode.values());
        menu.setSelected(configuration.getMode());
        menu.setEnabled(configuration.isEnabled());

        DropDownElement<TargetEspMode> modeElement = new DropDownElement<>("Highlight mode", menu);
        modeElement.setChangeListener(mode -> {
            if (mode == null) {
                return;
            }
            configuration.setMode(mode);
            persistConfiguration();
        });
        modeElement.bindDescription("Choose how the current target should be highlighted.");
        IconData enableIcon = new IconData(Material.ENDER_EYE);
        BooleanElement enabledToggle = new BooleanElement("Enable Target ESP", enableIcon, enabled -> {
            configuration.setEnabled(enabled);
            menu.setEnabled(enabled);
            if (!enabled) {
                controller.shutdown();
            }
            persistConfiguration();
        }, configuration.isEnabled());
        enabledToggle.bindDescription("Toggles highlighting for the last attacked entity.");
        list.add(enabledToggle);
        list.add(modeElement);
    }

    public TargetEspConfig configuration() {
        return configuration;
    }

    private void persistConfiguration() {
        com.google.gson.JsonObject config = getConfig();
        if (config != null) {
            configuration.save(config);
        }
        saveConfig();
    }
}
