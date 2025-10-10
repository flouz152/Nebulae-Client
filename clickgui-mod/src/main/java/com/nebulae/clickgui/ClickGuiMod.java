package com.nebulae.clickgui;

import com.nebulae.clickgui.client.ClickGuiClient;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ClickGuiMod.MOD_ID)
public class ClickGuiMod {
    public static final String MOD_ID = "nebulae_clickgui";

    public ClickGuiMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::onClientSetup);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(ClickGuiClient::init);
    }
}
