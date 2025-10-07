package beame.labyaddon;

import beame.labyaddon.config.NebulaeAddonConfig;
import beame.labyaddon.feature.FTHelperBridge;
import beame.labyaddon.feature.TargetESPBridge;
import beame.labyaddon.ui.NebulaeAddonSettingsGui;
import net.labymod.api.LabyAddon;
import net.labymod.api.configuration.loader.ConfigBuilder;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.gui.screen.ScreenOpenEvent;
import net.labymod.api.event.client.lifecycle.GameTickEvent;
import net.labymod.api.event.client.settings.SettingsOpenEvent;
import net.labymod.api.reference.annotation.Referenceable;
import net.labymod.api.util.io.LabyModFile;
import net.minecraft.client.Minecraft;

/**
 * LabyMod 3 add-on entry point which exposes the Essence FTHelper and TargetESP modules
 * via a compact configuration screen.
 */
@Referenceable
public class NebulaeAddon extends LabyAddon<NebulaeAddonConfig> {

    private final FTHelperBridge ftHelperBridge = new FTHelperBridge();
    private final TargetESPBridge targetESPBridge = new TargetESPBridge();

    @Override
    protected void enable() {
        this.ftHelperBridge.refresh();
        this.targetESPBridge.refresh();
    }

    @Override
    protected void disable() {
        // Nothing to clean up, the bridges are lightweight and stateless.
    }

    @Override
    protected Class<NebulaeAddonConfig> configurationClass() {
        return NebulaeAddonConfig.class;
    }

    @Override
    protected void fillSettings(java.util.List<net.labymod.api.client.gui.screen.widget.widgets.settings.SettingWidget> settings) {
        settings.add(net.labymod.api.client.gui.screen.widget.widgets.settings.CategorySettingWidget.create("Nebulae"));
        settings.add(net.labymod.api.client.gui.screen.widget.widgets.settings.ButtonSettingWidget.builder()
                .label("Открыть меню настроек")
                .action(() -> Minecraft.getInstance().displayGuiScreen(
                        new NebulaeAddonSettingsGui(Minecraft.getInstance().currentScreen, ftHelperBridge, targetESPBridge)))
                .build());
    }

    @Subscribe
    public void onSettingsOpen(SettingsOpenEvent event) {
        // Keep bridge state in sync whenever the settings are opened.
        this.ftHelperBridge.refresh();
        this.targetESPBridge.refresh();
    }

    @Subscribe
    public void onScreenOpened(ScreenOpenEvent event) {
        if (event.getScreen() instanceof NebulaeAddonSettingsGui) {
            // Screen already uses live module references, nothing else required.
            return;
        }
    }

    @Subscribe
    public void onClientTick(GameTickEvent event) {
        // Ensure queued actions are flushed once the world becomes available.
        if (Minecraft.getInstance().world != null && Minecraft.getInstance().player != null) {
            this.ftHelperBridge.flushQueuedState();
            this.targetESPBridge.flushQueuedState();
        }
    }

    @Override
    protected void saveConfig(LabyModFile file, ConfigBuilder builder, NebulaeAddonConfig config) {
        super.saveConfig(file, builder, config);
    }

    @Override
    protected NebulaeAddonConfig createConfig(ConfigBuilder builder) {
        NebulaeAddonConfig config = super.createConfig(builder);
        return config != null ? config : new NebulaeAddonConfig();
    }
}
