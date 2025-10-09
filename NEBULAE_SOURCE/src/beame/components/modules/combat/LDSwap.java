package beame.components.modules.combat;

import beame.module.Category;
import beame.module.Module;
import beame.util.math.TimerUtil;
import beame.util.player.InventoryUtility;
import events.Event;
import events.EventKey;
import events.impl.player.EventUpdate;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

public class LDSwap extends Module {

    private final TimerUtil swapTimer = new TimerUtil();
    private boolean queued;

    public LDSwap() {
        super("LDSwap", Category.Combat, true, "Меняет нагрудник с обходом");
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
            if (!keyEvent.isReleased() && keyEvent.key == GLFW.GLFW_KEY_G) {
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

        if (mc.player.openContainer == null) {
            return;
        }

        InventoryUtility.moveItem(slot, 6);
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
                bestSlot = (i < 9) ? i + 36 : i;
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