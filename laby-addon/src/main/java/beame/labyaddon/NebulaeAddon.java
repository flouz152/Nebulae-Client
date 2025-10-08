package beame.labyaddon;

import beame.labyaddon.config.NebulaeAddonConfig;
import beame.labyaddon.core.ModuleManager;
import beame.labyaddon.module.render.TargetESPModule;
import beame.labyaddon.ui.NebulaeAddonSettingsGui;
import net.labymod.api.LabyAddon;
import net.labymod.api.configuration.loader.ConfigBuilder;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.settings.SettingsOpenEvent;
import net.labymod.api.reference.annotation.Referenceable;
import net.labymod.api.util.io.LabyModFile;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

/**
 * LabyMod 3 add-on entry point which exposes the Essence TargetESP module via a
 * compact configuration screen.
 */
@Referenceable
public class NebulaeAddon extends LabyAddon<NebulaeAddonConfig> {

    private final ModuleManager moduleManager = new ModuleManager();
    private final TargetESPModule targetEspModule = new TargetESPModule();
    private final ForgeBridge forgeBridge = new ForgeBridge();

    @Override
    protected void enable() {
        moduleManager.clear();
        moduleManager.register(targetEspModule);
        MinecraftForge.EVENT_BUS.register(forgeBridge);

        NebulaeAddonConfig config = configuration();
        targetEspModule.applyConfig(config);
    }

    @Override
    protected void disable() {
        NebulaeAddonConfig config = configuration();
        targetEspModule.exportConfig(config);
        MinecraftForge.EVENT_BUS.unregister(forgeBridge);
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
                        new NebulaeAddonSettingsGui(Minecraft.getInstance().currentScreen, configuration(), targetEspModule)))
                .build());
    }

    @Subscribe
    public void onSettingsOpen(SettingsOpenEvent event) {
        // nothing else required – modules live entirely inside the addon now.
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

    private class ForgeBridge {

        @SubscribeEvent
        public void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                moduleManager.onTick();
            }
        }

        @SubscribeEvent
        public void onChat(ClientChatReceivedEvent event) {
            if (event.getMessage() != null) {
                moduleManager.onChatMessage(event.getMessage());
            }
        }

        @SubscribeEvent
        public void onRenderWorld(RenderWorldLastEvent event) {
            moduleManager.onRender3D(event.getMatrixStack(), event.getPartialTicks());
        }

        @SubscribeEvent
        public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
            if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
                moduleManager.onRender2D(event.getMatrixStack(), event.getPartialTicks());
            }
        }
    }
}
