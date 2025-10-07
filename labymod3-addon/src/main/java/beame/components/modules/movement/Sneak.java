package beame.components.modules.movement;

import events.Event;
import events.impl.player.EventInput;
import events.impl.player.EventMove;
import lombok.Getter;
import lombok.experimental.Accessors;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.network.play.client.CEntityActionPacket;
import org.apache.commons.lang3.time.StopWatch;


@Getter
@Accessors(fluent = true)

public class Sneak extends Module {
// leaked by itskekoff; discord.gg/sk3d IiqU7Uew
    private final StopWatch time = new StopWatch();
    
    public Sneak() {
        super("Sneak", Category.Movement, true, "Отключает медленную ходьбу при зажатой кнопке Shift");
    }

    @Override
    public void onDisable() {
        assert mc.player != null;
        if (!mc.player.isSneaking()) {
            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.RELEASE_SHIFT_KEY));
        }
    }

    @Override
    public void event(Event e) {
        if (e instanceof EventMove) {
            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                if ((mc.gameSettings.keyBindJump.isKeyDown()) && mc.player.isOnGround()) {
                    mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.RELEASE_SHIFT_KEY));
                } else {
                    mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.PRESS_SHIFT_KEY));
                }
                mc.player.movementInput.sneaking = true;
            }
        } else if (e instanceof EventInput) {
            EventInput event = (EventInput) e;
            event.setSneakSlow(1);
        }
    }
}

