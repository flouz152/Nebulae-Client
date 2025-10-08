package beame.components.modules.player;

import beame.components.command.AbstractCommand;
import events.Event;
import events.EventKey;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import beame.setting.SettingList.BindSetting;

public class MiddleClickPearl extends Module {
// leaked by itskekoff; discord.gg/sk3d qTrllU0R
    public MiddleClickPearl() {
        super("ClickPearl", Category.Player, true, "Пакетный выброс эндер-жемчуга через клавишу");
        addSettings(throwBind);
    }

    public BindSetting throwBind = new BindSetting("Кинуть пёрл", 0);

    @Override
    public void event(Event event) {
        if(event instanceof EventKey) {
           if(((EventKey) event).key == throwBind.get()) {
                handleThrowPearl();
            }
        }
    }

    public void handleThrowPearl() {
        if (mc.player == null || mc.playerController == null || mc.player.connection == null) {
            return;
        }
        if (!mc.player.getCooldownTracker().hasCooldown(Items.ENDER_PEARL)) {
            if (mc.player.getHeldItemOffhand().getItem() instanceof EnderPearlItem) {
                mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
                // mc.player.swingArm(Hand.OFF_HAND);
                return;
            }

            int originalSlot = mc.player.inventory.currentItem;
            int pearlSlot = -1;

            for (int i = 0; i < 9; ++i) {
                if (mc.player.inventory.getStackInSlot(i).getItem() == Items.ENDER_PEARL) {
                    pearlSlot = i;
                    break;
                }
            }

            if (pearlSlot != -1) {
                if (pearlSlot != originalSlot) {
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(pearlSlot));
                }
                mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                mc.player.swingArm(Hand.MAIN_HAND);
                if (pearlSlot != originalSlot) {
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(originalSlot));
                }
                return;
            }

            for (int i = 9; i < 36; ++i) {
                if (mc.player.inventory.getStackInSlot(i).getItem() == Items.ENDER_PEARL) {
                    pearlSlot = i;
                    break;
                }
            }

            if (pearlSlot != -1) {
                mc.playerController.pickItem(pearlSlot);
                mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                mc.player.swingArm(Hand.MAIN_HAND);
                mc.playerController.pickItem(pearlSlot);
                return;
            }
             AbstractCommand.addMessage("Эндер-жемчуг не найден!");
        }
    }
}
