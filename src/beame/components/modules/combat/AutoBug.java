package beame.components.modules.combat;

import beame.module.Category;
import beame.module.Module;
import events.Event;
import events.impl.player.EventUpdate;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraft.network.play.client.CCloseWindowPacket;

public class AutoBug extends Module {
    private boolean triggered;

    public AutoBug() {
        super("AutoBug", Category.Combat, true, "Открывает и закрывает инвентарь при получении урона");
    }

    @Override
    protected void onDisable() {
        triggered = false;
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventUpdate) {
            if (mc.player == null || mc.player.connection == null) {
                return;
            }

            boolean hurt = mc.player.hurtTime > 0;
            if (hurt && !triggered) {
                triggered = true;

                if (mc.player.openContainer != null && mc.player.openContainer.windowId == 0) {
                    mc.player.connection.sendPacket(new CClientStatusPacket(CClientStatusPacket.State.OPEN_INVENTORY_ACHIEVEMENT));
                    mc.player.connection.sendPacket(new CCloseWindowPacket(mc.player.openContainer.windowId));
                }
            } else if (!hurt) {
                triggered = false;
            }
        }
    }
}
