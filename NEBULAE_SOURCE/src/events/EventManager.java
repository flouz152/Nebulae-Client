package events;

import beame.Nebulae;
import beame.module.Module;
import net.minecraft.client.Minecraft;


public class EventManager {
// leaked by itskekoff; discord.gg/sk3d zAFokzyg
    public static void call(final Event event) {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().world == null) {
            return;
        }

        if(!Nebulae.getHandler().unhooked) {
            if (event.isCancel())
                return;

            callEvent(event);
        }
    }

    private static void callEvent(Event event) {
        for (final Module module : Nebulae.getHandler().getModuleList().getModules()) {
            if (!module.isState())
                continue;

            module.event(event);
        }

        Nebulae.getHandler().auraHelper.callAll(event);
    }
}