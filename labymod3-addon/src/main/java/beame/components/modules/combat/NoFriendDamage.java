package beame.components.modules.combat;

import beame.Essence;
import events.Event;
import events.impl.packet.EventPacket;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CUseEntityPacket;

public class NoFriendDamage extends Module {
// leaked by itskekoff; discord.gg/sk3d K96OwaYD
    public NoFriendDamage() {
        super("NoFriendDamage", Category.Combat, true, "Позволяет ударам не проходить по друзьям");
    }

    @Override
    public void event(Event event) {
        if(event instanceof EventPacket) {
            EventPacket e = (EventPacket) event;
            if (e.getPacket() instanceof CUseEntityPacket) {
                CUseEntityPacket cUseEntityPacket = (CUseEntityPacket) e.getPacket();
                Entity entity = cUseEntityPacket.getEntityFromWorld(mc.world);
                if (entity instanceof RemoteClientPlayerEntity && Essence.getHandler().friends.isFriend(entity.getScoreboardName()) && cUseEntityPacket.getAction() == CUseEntityPacket.Action.ATTACK) {
                    e.setCancel(true);
                }
            }
        }
    }
}
