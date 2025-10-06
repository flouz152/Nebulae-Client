package beame.components.modules.combat;

import beame.module.Category;
import beame.module.Module;
import events.Event;
import events.impl.player.EventUpdate;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.client.CEntityActionPacket;

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

                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.OPEN_INVENTORY));
                mc.player.connection.sendPacket(new CCloseWindowPacket(0));
            } else if (!hurt) {
                triggered = false;
            }
        }
    }
}
