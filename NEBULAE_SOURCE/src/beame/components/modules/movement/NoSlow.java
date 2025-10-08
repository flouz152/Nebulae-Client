package beame.components.modules.movement;

import beame.util.math.MathUtil;
import beame.util.math.MovementUtil;
import beame.util.math.TimerUtil;
import events.Event;
import events.impl.player.EventSlowWalking;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.UseAction;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import beame.setting.SettingList.RadioSetting;
import net.minecraft.item.Items;

public class NoSlow extends Module {
// leaked by itskekoff; discord.gg/sk3d 4xfUc9N0
    public RadioSetting mode = new RadioSetting("Режим", "СпукиТайм", "Ванильный","Снежные ковры", "Спуки/Холик", "Грим");

    public NoSlow() {
        super("NoSlow", Category.Movement, true, "Отключает замедление игрока при использовании еды, и т.д.");
        addSettings(mode);
    }

    TimerUtil stopWatch = new TimerUtil();

    @Override
    public void event(Event e) {
        if (e instanceof EventSlowWalking event) {
            if (mc.player == null || mc.player.isElytraFlying()) return;
            if (mode.get("Снежные ковры")) {
                Block cur = mc.world.getBlockState(new BlockPos(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ())).getBlock();
                if (cur != Blocks.SNOW && cur != Blocks.TORCH && !(cur instanceof net.minecraft.block.CarpetBlock))
                    return;

                if ((mc.player.getHeldItemMainhand().getUseAction() == UseAction.BLOCK && mc.player.getActiveHand() == Hand.OFF_HAND) ||
                        (mc.player.getHeldItemOffhand().getUseAction() == UseAction.BLOCK && mc.player.getActiveHand() == Hand.MAIN_HAND) ||
                        (mc.player.getHeldItemMainhand().getUseAction() == UseAction.EAT && mc.player.getActiveHand() == Hand.OFF_HAND) ||
                        (mc.player.getHeldItemOffhand().getUseAction() == UseAction.EAT && mc.player.getActiveHand() == Hand.MAIN_HAND)) {
                    return;
                }

                if (mc.player.getActiveHand() == Hand.MAIN_HAND) {
                    mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
                    event.setCancel(true);
                    return;
                } else if (mc.player.getActiveHand() == Hand.OFF_HAND) {
                    mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    event.setCancel(true);
                    return;
                }
                event.setCancel(true);
                sendItemChangePacket();
            } else if (mode.get("Ванильный")) {
                event.setCancel(true);
            } else if(mode.get("Спуки/Холик")) {
                float z1 = MathUtil.random(70,80);
                if (stopWatch.hasReached(80)) {
                    event.cancel();
                    mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    stopWatch.reset();
                }
            } else if(mode.get("Грим")) {
                boolean offHandActive = mc.player.isHandActive() && mc.player.getActiveHand() == Hand.OFF_HAND;
                boolean mainHandActive = mc.player.isHandActive() && mc.player.getActiveHand() == Hand.MAIN_HAND;

                if (!(mc.player.getItemInUseCount() < 25 && mc.player.getItemInUseCount() > 4) && mc.player.getHeldItemOffhand().getItem() != Items.SHIELD) return;

                if (mc.player.isHandActive() && !mc.player.isPassenger()) {
                    mc.playerController.syncCurrentPlayItem();

                    if (offHandActive && !mc.player.getCooldownTracker().hasCooldown(mc.player.getHeldItemOffhand().getItem())) {
                        int old = mc.player.inventory.currentItem;

                        mc.player.connection.sendPacket(new CHeldItemChangePacket(old + 1 > 8 ? old - 1 : old + 1));
                        mc.player.connection.sendPacketWithoutEvent(new CHeldItemChangePacket(mc.player.inventory.currentItem));

                        mc.player.setSprinting(false);

                        event.setCancel(true);
                    }

                    if (mainHandActive && !mc.player.getCooldownTracker().hasCooldown(mc.player.getHeldItemMainhand().getItem())) {
                        mc.player.connection.sendPacketWithoutEvent(new CPlayerTryUseItemPacket(Hand.OFF_HAND));

                        mc.player.setSprinting(false);
                        if (mc.player.getHeldItemOffhand().getUseAction().equals(UseAction.NONE)) event.setCancel(true);
                    }

                    mc.playerController.syncCurrentPlayItem();
                }
            }
        }
    }


    public boolean isBlockUnderWithMotion() {
        AxisAlignedBB aab = mc.player.getBoundingBox().offset(mc.player.getMotion().x, -1e-1, mc.player.getMotion().z);
        return mc.world.getCollisionShapes(mc.player, aab).toList().isEmpty();
    }

    private void sendItemChangePacket() {
        if (MovementUtil.isMoving()) {
            mc.player.connection.sendPacket(new CHeldItemChangePacket((mc.player.inventory.currentItem % 8 + 1)));
            mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
        }
    }
}
