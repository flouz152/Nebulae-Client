package beame.laby.targetesp;

import net.labymod.gui.elements.DropDownMenu;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.DropDownElement;
import net.labymod.settings.elements.HeaderElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Material;

import java.util.List;

public class TargetEspConfig {

    private static final String ENABLED_KEY = "enabled";
    private static final String MODE_KEY = "mode";

    private final TargetEspAddon addon;

    private boolean enabled = true;
    private TargetEspMode mode = TargetEspMode.GHOSTS;

    public TargetEspConfig(TargetEspAddon addon) {
        this.addon = addon;
    }

    public void load() {
        if (addon.getConfig().has(ENABLED_KEY)) {
            enabled = addon.getConfig().get(ENABLED_KEY).getAsBoolean();
        }
        if (addon.getConfig().has(MODE_KEY)) {
            mode = TargetEspMode.fromConfigValue(addon.getConfig().get(MODE_KEY).getAsString());
        }
        save();
    }

    public void fillSettings(List<SettingsElement> settings) {
        settings.add(new HeaderElement("Target ESP"));
        settings.add(new BooleanElement("Enabled", new ControlElement.IconData(Material.LEVER), value -> {
            enabled = value;
            save();
        }, enabled));

        DropDownMenu<TargetEspMode> modeMenu = new DropDownMenu<>("Mode", 0, 0, 0, 0).fill(TargetEspMode.values());
        modeMenu.setSelected(mode);

        DropDownElement<TargetEspMode> modeElement = new DropDownElement<>("Mode", modeMenu);
        modeElement.setChangeListener(selected -> {
            mode = selected;
            save();
        });
        settings.add(modeElement);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public TargetEspMode getMode() {
        return mode;
    }

    private void save() {
        addon.getConfig().addProperty(ENABLED_KEY, enabled);
        addon.getConfig().addProperty(MODE_KEY, mode.getConfigKey());
    }
}