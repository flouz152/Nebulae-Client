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
        DropDownMenu<TargetEspMode> menu = new DropDownMenu<>("Режим подсветки", 0, 0, 0, 0);
        menu.fill(TargetEspMode.values());
        menu.setSelected(configuration.getMode());
        menu.setEnabled(configuration.isEnabled());

        DropDownElement<TargetEspMode> modeElement = new DropDownElement<>("Режим подсветки", menu);
        modeElement.setChangeListener(mode -> {
            if (mode == null) {
                return;
            }
            configuration.setMode(mode);
            persistConfiguration();
        });
        modeElement.bindDescription("Выберите визуальный стиль подсветки цели.");
        IconData enableIcon = new IconData(Material.ENDER_EYE);
        BooleanElement enabledToggle = new BooleanElement("Включить Target ESP", enableIcon, enabled -> {
            configuration.setEnabled(enabled);
            menu.setEnabled(enabled);
            if (!enabled) {
                controller.shutdown();
            }
            persistConfiguration();
        }, configuration.isEnabled());
        enabledToggle.bindDescription("Активирует подсветку последней атакованной цели.");
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
