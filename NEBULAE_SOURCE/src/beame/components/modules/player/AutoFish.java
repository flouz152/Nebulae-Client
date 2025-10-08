package beame.components.modules.player;

import beame.util.math.TimerUtil2;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.util.Hand;
import events.Event;
import events.impl.packet.EventPacket;
import events.impl.player.EventUpdate;
import beame.module.Module;
import beame.module.Category;


public class AutoFish extends Module {
// leaked by itskekoff; discord.gg/sk3d mg1KVqMI
    private final TimerUtil2 delay = new TimerUtil2();
    private boolean isHooked = false;
    private boolean needToHook = false;

    public AutoFish() {
        super("AutoFish", Category.Player, true, "Автоматически ловит рыбу");
    }

    @Override
    public void toggle() {
        super.toggle();
        delay.reset();
        isHooked = false;
        needToHook = false;
    }

    @Override
    public void event(Event event) {
        if (mc.player == null) return;
        
        if (event instanceof EventPacket) {
            if (((EventPacket) event).getPacket() instanceof SPlaySoundEffectPacket wrapper) {
                if (wrapper.getSound().getName().getPath().equals("entity.fishing_bobber.splash")) {
                    if (mc.player.fishingBobber == null) return;
                    
                    double dx = mc.player.fishingBobber.getPosX() - wrapper.getX();
                    double dy = mc.player.fishingBobber.getPosY() - wrapper.getY();
                    double dz = mc.player.fishingBobber.getPosZ() - wrapper.getZ();
                    double distSq = dx * dx + dy * dy + dz * dz;
                    
                    if (distSq <= 0.2f) {
                        mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                        needToHook = true;
                        delay.reset();
                    }
                }
            }
        }

        if (event instanceof EventUpdate) {
            if (delay.isReached(200) && needToHook) {
                mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                needToHook = false;
                delay.reset();
            }
        }
    }
}
