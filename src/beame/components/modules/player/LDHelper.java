package beame.components.modules.player;

import beame.components.command.AbstractCommand;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.BindSetting;
import beame.util.math.TimerUtil;
import beame.util.player.InventoryUtility;
import beame.util.player.KeyboardUtil;
import events.Event;
import events.EventKey;
import events.impl.player.EventUpdate;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CClickWindowPacket;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;

public class LDHelper extends Module {
    private static final long STAL_USE_INTERVAL_MS = 150L;

    private final BindSetting glowstoneBind = new BindSetting("Glowstone пыль", KeyboardUtil.KEY_PERIOD.keyCode);
    private final BindSetting stalBind = new BindSetting("C418 — stal", -1);

    private final TimerUtil stalTimer = new TimerUtil();
    private boolean stalActive;

    public LDHelper() {
        super("LDHelper", Category.Player, true, "Помощник для быстрого использования LD предметов.");
        addSettings(glowstoneBind, stalBind);
    }

    @Override
    protected void onEnable() {
        stalTimer.reset();
        stalActive = false;
    }

    @Override
    protected void onDisable() {
        stalActive = false;
    }

    @Override
    public void event(Event event) {
        if (mc.player == null || mc.playerController == null || mc.player.connection == null) {
            return;
        }

        if (event instanceof EventKey keyEvent) {
            if (mc.currentScreen instanceof ChatScreen) {
                return;
            }

            if (keyEvent.key == glowstoneBind.get() && !keyEvent.isReleased()) {
                if (!useItemInstant(Items.GLOWSTONE_DUST)) {
                    AbstractCommand.addMessage("Glowstone пыль не найдена!");
                }
            }

            if (keyEvent.key == stalBind.get()) {
                if (!keyEvent.isReleased()) {
                    stalActive = true;
                    stalTimer.reset();
                    if (!useItemInstant(Items.MUSIC_DISC_STAL)) {
                        AbstractCommand.addMessage("Пластинка C418 — Stal не найдена!");
                        stalActive = false;
                    }
                } else {
                    stalActive = false;
                }
            }
            return;
        }

        if (event instanceof EventUpdate && stalActive && stalTimer.passed(STAL_USE_INTERVAL_MS)) {
            if (!useItemInstant(Items.MUSIC_DISC_STAL)) {
                AbstractCommand.addMessage("Пластинка C418 — Stal не найдена!");
                stalActive = false;
            } else {
                stalTimer.reset();
            }
        }
    }

    private boolean useItemInstant(Item item) {
        return useItemPacket(item);
    }

    private boolean useItemPacket(Item item) {
        if (mc.player == null || mc.player.connection == null || mc.player.openContainer == null) {
            return false;
        }

        int slotId = InventoryUtility.getSlotIDFromItem(item);
        if (slotId < 0 || slotId == -2) {
            return false;
        }

        Container container = mc.player.openContainer;
        int windowId = container.windowId;

        int selectedSlot = mc.player.inventory.currentItem;
        int selectedContainerSlot = 36 + selectedSlot;
        boolean needsSwap = slotId != selectedContainerSlot;

        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.OPEN_INVENTORY));

        if (isHotbarSlot(slotId)) {
            int hotbarSlot = slotId - 36;
            sendHeldItemChange(hotbarSlot);
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            sendHeldItemChange(selectedSlot);
        } else {
            if (needsSwap) {
                sendSwap(container, windowId, slotId, selectedSlot);
            }

            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));

            if (needsSwap) {
                sendSwap(container, windowId, slotId, selectedSlot);
            }
        }

        mc.player.connection.sendPacket(new CCloseWindowPacket(windowId));
        return true;
    }

    private boolean isHotbarSlot(int slotId) {
        return slotId >= 36 && slotId <= 44;
    }

    private void sendHeldItemChange(int hotbarSlot) {
        if (hotbarSlot < 0 || hotbarSlot > 8) {
            return;
        }

        mc.player.connection.sendPacket(new CHeldItemChangePacket(hotbarSlot));
    }

    private void sendSwap(Container container, int windowId, int slotId, int hotbarSlot) {
        short transaction = container.getNextTransactionID(mc.player.inventory);
        mc.player.connection.sendPacket(new CClickWindowPacket(
                windowId,
                slotId,
                hotbarSlot,
                ClickType.SWAP,
                ItemStack.EMPTY,
                transaction
        ));
    }
}
