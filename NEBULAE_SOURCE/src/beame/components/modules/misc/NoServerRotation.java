package beame.components.modules.misc;

import events.Event;
import events.impl.packet.EventPacket;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;

public class NoServerRotation extends Module {
// leaked by itskekoff; discord.gg/sk3d tnb4EAn0

    public NoServerRotation() {
        super("NoServerRotation", Category.Misc, true, "Запрещает серверу менять положение вашей камеры");
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventPacket eventPacket) {
            if (eventPacket.isReceivePacket()) {
                if (eventPacket.getPacket() instanceof SPlayerPositionLookPacket packet) {
                    packet.yaw = mc.player.rotationYaw;
                    packet.pitch = mc.player.rotationPitch;
                }
            }
        }
    }

}