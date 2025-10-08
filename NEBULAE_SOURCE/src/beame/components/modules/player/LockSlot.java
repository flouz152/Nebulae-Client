package beame.components.modules.player;

import events.Event;
import events.impl.packet.EventPacket;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CClickWindowPacket;
import net.minecraft.inventory.container.ClickType;
import beame.setting.SettingList.EnumSetting;
import beame.setting.SettingList.BooleanSetting;

public class LockSlot extends Module {
// leaked by itskekoff; discord.gg/sk3d XDVLALiu

    public final EnumSetting lockSlots = new EnumSetting("Заблокированные слоты",
            new BooleanSetting("Слот 1", false),
            new BooleanSetting("Слот 2", false),
            new BooleanSetting("Слот 3", false),
            new BooleanSetting("Слот 4", false),
            new BooleanSetting("Слот 5", false),
            new BooleanSetting("Слот 6", false),
            new BooleanSetting("Слот 7", false),
            new BooleanSetting("Слот 8", false),
            new BooleanSetting("Слот 9", false)
    );

    private boolean wasDropPressed = false;

    public LockSlot() {
        super("LockSlot", Category.Player, true, "Отключает выброс предметов через клавишу Q");
        addSettings(lockSlots);
    }

    @Override
    public void event(Event event) {
        if (mc.player == null) return;

        if (event instanceof EventPacket) {
            EventPacket e = (EventPacket) event;

            if (e.isSendPacket() && e.getPacket() instanceof CPlayerDiggingPacket) {
                CPlayerDiggingPacket packet = (CPlayerDiggingPacket) e.getPacket();

                if (packet.getAction() == CPlayerDiggingPacket.Action.DROP_ITEM ||
                        packet.getAction() == CPlayerDiggingPacket.Action.DROP_ALL_ITEMS) {

                    int currentSlot = mc.player.inventory.currentItem;
                    if (isSlotLocked(currentSlot)) {
                        e.setCancel(true);
                        mc.gameSettings.keyBindDrop.setPressed(false);


                        updateSlot(currentSlot);
                    }
                }
            }
        }


        if (mc.gameSettings.keyBindDrop.isKeyDown()) {
            if (!wasDropPressed) {
                wasDropPressed = true;
                int currentSlot = mc.player.inventory.currentItem;
                if (isSlotLocked(currentSlot)) {
                    mc.gameSettings.keyBindDrop.setPressed(false);
                    updateSlot(currentSlot);
                }
            }
        } else {
            wasDropPressed = false;
        }
    }

    private void updateSlot(int slot) {
        if (mc.player != null && mc.player.openContainer != null) {

            mc.player.connection.sendPacket(new CClickWindowPacket(
                    mc.player.openContainer.windowId,
                    36 + slot,
                    0,
                    ClickType.PICKUP,
                    mc.player.inventory.getStackInSlot(slot),
                    mc.player.openContainer.getNextTransactionID(mc.player.inventory)
            ));



            mc.player.connection.sendPacket(new CClickWindowPacket(
                    mc.player.openContainer.windowId,
                    36 + slot,
                    0,
                    ClickType.PICKUP,
                    mc.player.inventory.getStackInSlot(slot),
                    mc.player.openContainer.getNextTransactionID(mc.player.inventory)
            ));
        }
    }

    private boolean isSlotLocked(int slot) {
        return lockSlots.get("Слот " + (slot + 1)).get();
    }
}