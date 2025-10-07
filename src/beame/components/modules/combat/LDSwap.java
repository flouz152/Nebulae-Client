package beame.components.modules.combat;

import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.BindSetting;
import beame.util.math.TimerUtil;
import events.Event;
import events.EventKey;
import events.impl.player.EventUpdate;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class LDSwap extends Module {

    private final BindSetting swapBind = new BindSetting("Кнопка свапа", 0);
    private final TimerUtil swapTimer = new TimerUtil();
    private boolean queued;

    public LDSwap() {
        super("LDSwap", Category.Combat, true, "Меняет нагрудник с обходом");
        addSettings(swapBind);
        swapTimer.reset();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        queued = false;
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventKey keyEvent) {
            if (!keyEvent.isReleased() && keyEvent.key == swapBind.get()) {
                queued = true;
            }
        } else if (event instanceof EventUpdate) {
            if (queued) {
                queued = false;
                trySwap();
            }
        }
    }

    private void trySwap() {
        if (mc.player == null || mc.world == null || mc.currentScreen != null) {
            return;
        }
        if (!swapTimer.hasReached(180L)) {
            return;
        }

        ItemStack current = mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        int slot = findDiamondChestplate(current);
        if (slot == -1) {
            return;
        }

        if (!(mc.player.openContainer instanceof PlayerContainer container)) {
            return;
        }

        int containerSlot = slot < 9 ? slot + 36 : slot;
        mc.playerController.windowClick(container.windowId, containerSlot, 38, ClickType.SWAP, mc.player);
        mc.player.swingArm(Hand.MAIN_HAND);
        swapTimer.reset();
    }

    private int findDiamondChestplate(ItemStack current) {
        int bestSlot = -1;
        int bestDamage = Integer.MAX_VALUE;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty() || stack.getItem() != Items.DIAMOND_CHESTPLATE) {
                continue;
            }
            if (isSameAsCurrent(current, stack)) {
                continue;
            }

            int damage = stack.isDamageable() ? stack.getDamage() : 0;
            if (damage < bestDamage) {
                bestDamage = damage;
                bestSlot = i;
            }
        }

        return bestSlot;
    }

    private boolean isSameAsCurrent(ItemStack current, ItemStack stack) {
        if (current.isEmpty()) {
            return false;
        }
        if (!ItemStack.areItemsEqual(current, stack)) {
            return false;
        }
        if (current.isDamageable()) {
            return stack.isDamageable() && stack.getDamage() == current.getDamage();
        }
        return ItemStack.areItemStackTagsEqual(current, stack);
    }
}
