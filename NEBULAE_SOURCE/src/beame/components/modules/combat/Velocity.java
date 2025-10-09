package beame.components.modules.combat;

import beame.util.other.Script;
import beame.util.player.PlayerUtil;
import events.Event;
import events.impl.packet.EventPacket;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import beame.setting.SettingList.RadioSetting;

public class Velocity extends Module {
// leaked by itskekoff; discord.gg/sk3d SpNpstd2
    private final RadioSetting mode = new RadioSetting( "Режим", "FunTime", "Default","FunTime");
    private final Script script = new Script();

    public Velocity() {
     super ("Velocity", Category.Combat, true, "Убирает отталкивание у вашего персонажа");
     addSettings(mode);
    }

    @Override
    public void event(Event e) {
        if(e instanceof EventPacket event) {
            if (PlayerUtil.nullCheck()) return;
            final IPacket<?> packet = event.getPacket();

            if (packet instanceof SEntityVelocityPacket wrapper && wrapper.getEntityID() == mc.player.getEntityId()) {
                switch (mode.get()) {
                    case "FunTime" -> {
                        if (script.isFinished() && !mc.player.isHandActive()) {
                            BlockPos.getAllInBox(mc.player.getBoundingBox().offset(0, -1e-4, 0)).forEach(pos -> {
                                mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));
                            });
                            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.PRESS_SHIFT_KEY));
                            event.cancel();
                            script.cleanup().addTickStep(0, () -> mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.RELEASE_SHIFT_KEY)));
                        }
                    }
                    case "Default" -> event.cancel();
                }
            }
        }
        if(e instanceof EventUpdate) {
            script.update();
        }
    }
}
