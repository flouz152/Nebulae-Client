package nebulae.addon;

import beame.components.modules.player.FTHelper;
import beame.components.modules.render.TargetESP;
import nebulae.addon.config.NebulaeAddonConfig;
import nebulae.addon.gui.ModuleScreen;
import net.labymod.api.addon.LabyAddon;
import net.labymod.api.addon.annotation.AddonMain;
import net.labymod.api.client.keymapping.KeyMapping;
import net.labymod.api.client.keymapping.KeyMappingRegistry;
import net.labymod.api.client.resources.ResourceLocation;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.lifecycle.GameTickEvent;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

@AddonMain
public class NebulaeAddon extends LabyAddon<NebulaeAddonConfig> {

    private final Map<KeyMapping, Runnable> moduleScreens = new HashMap<>();
    private FTHelper ftHelper;
    private TargetESP targetESP;

    @Override
    protected void preEnable() {
        this.ftHelper = new FTHelper();
        this.targetESP = new TargetESP();
    }

    @Override
    protected void enable() {
        KeyMappingRegistry registry = this.registerKeyMappingRegistry();

        KeyMapping ftHelperMenu = KeyMapping.builder()
            .id(ResourceLocation.create("nebulae", "fthelper_menu"))
            .displayName("Nebulae FTHelper Menu")
            .key(GLFW.GLFW_KEY_KP_7)
            .category("Nebulae Addon")
            .build();

        KeyMapping targetEspMenu = KeyMapping.builder()
            .id(ResourceLocation.create("nebulae", "targetesp_menu"))
            .displayName("Nebulae TargetESP Menu")
            .key(GLFW.GLFW_KEY_KP_8)
            .category("Nebulae Addon")
            .build();

        registry.register(ftHelperMenu);
        registry.register(targetEspMenu);

        moduleScreens.put(ftHelperMenu, () -> Minecraft.getInstance().displayGuiScreen(new ModuleScreen(ftHelper)));
        moduleScreens.put(targetEspMenu, () -> Minecraft.getInstance().displayGuiScreen(new ModuleScreen(targetESP)));

        this.registerListener(this);
    }

    @Override
    protected Class<? extends NebulaeAddonConfig> configClass() {
        return NebulaeAddonConfig.class;
    }

    @Subscribe
    public void onGameTick(GameTickEvent event) {
        moduleScreens.forEach((key, opener) -> {
            if (key.isPressed()) {
                opener.run();
            }
        });
    }
}
