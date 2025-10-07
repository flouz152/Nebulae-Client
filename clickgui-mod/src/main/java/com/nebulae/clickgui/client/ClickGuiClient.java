package com.nebulae.clickgui.client;

import com.nebulae.clickgui.client.gui.ClickGuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = com.nebulae.clickgui.ClickGuiMod.MOD_ID, value = Dist.CLIENT)
public final class ClickGuiClient {
    private static KeyBinding openKey;

    private ClickGuiClient() {
    }

    public static void init() {
        openKey = new KeyBinding(
                "key.nebulae_clickgui.open",
                KeyConflictContext.IN_GAME,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "key.categories.misc"
        );
        ClientRegistry.registerKeyBinding(openKey);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.currentScreen != null) {
            return;
        }

        if (openKey.isPressed()) {
            minecraft.displayGuiScreen(new ClickGuiScreen());
        }
    }
}
