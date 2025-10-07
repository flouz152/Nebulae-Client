package beame.components.modules.combat;

import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.BindSetting;
import beame.util.math.TimerUtil;
import beame.util.player.InventoryUtility;
import events.Event;
import events.EventKey;
import events.impl.player.EventUpdate;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LDSwap extends Module {

    private final BindSetting swapBind = new BindSetting("Кнопка свапа", 0);
    private final TimerUtil swapTimer = new TimerUtil();
    private boolean queued;
    private boolean swapping;

    private static final int CHEST_CONTAINER_SLOT = 7;

    public LDSwap() {
        super("LDSwap", Category.Combat, true, "Меняет нагрудник с обходом");
        addSettings(swapBind);
        swapTimer.setMs(0);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        queued = false;
        swapping = false;
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventKey keyEvent) {
            if (!keyEvent.isReleased() && keyEvent.key == swapBind.get()) {
                queued = true;
            }
        } else if (event instanceof EventUpdate) {
            if (queued && !swapping) {
                queued = false;
                trySwapChestplate();
            }
        }
    }

    private void trySwapChestplate() {
        if (mc.player == null || mc.world == null || mc.currentScreen != null || !swapTimer.hasReached(250)) {
            return;
        }

        ItemStack current = mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        int replacementSlot = findReplacementSlot(current);
        if (replacementSlot == -1) {
            return;
        }

        swapping = true;
        swapTimer.reset();
        int containerSlot = toContainerSlot(replacementSlot);
        boolean hadChestplateEquipped = !current.isEmpty();

        long initialDelay = hadChestplateEquipped ? ThreadLocalRandom.current().nextLong(35L, 75L) : 0L;
        long grabDelay = initialDelay + ThreadLocalRandom.current().nextLong(40L, 85L);
        long placeDelay = grabDelay + ThreadLocalRandom.current().nextLong(35L, 70L);
        long cleanupDelay = placeDelay + ThreadLocalRandom.current().nextLong(30L, 55L);

        if (hadChestplateEquipped) {
            scheduleInventoryAction(initialDelay, () -> InventoryUtility.pickupItem(CHEST_CONTAINER_SLOT, 0));
        }

        scheduleInventoryAction(grabDelay, () -> InventoryUtility.pickupItem(containerSlot, 0));
        scheduleInventoryAction(placeDelay, () -> InventoryUtility.pickupItem(CHEST_CONTAINER_SLOT, 0));
        scheduleInventoryAction(cleanupDelay, () -> {
            if (!mc.player.inventory.getItemStack().isEmpty()) {
                InventoryUtility.pickupItem(containerSlot, 0);
            }
            mc.player.swingArm(Hand.MAIN_HAND);
            swapping = false;
            swapTimer.reset();
        });
    }

    private int findReplacementSlot(ItemStack current) {
        List<Integer> candidates = IntStream.range(0, 36)
                .filter(i -> isChestplate(mc.player.inventory.getStackInSlot(i)))
                .boxed()
                .sorted(Comparator.comparingDouble((Integer slot) -> -scoreChestplate(mc.player.inventory.getStackInSlot(slot))))
                .collect(Collectors.toList());

        for (int slot : candidates) {
            ItemStack stack = mc.player.inventory.getStackInSlot(slot);
            if (current.isEmpty()) {
                return slot;
            }
            boolean sameItem = ItemStack.areItemsEqualIgnoreDurability(current, stack)
                    && ItemStack.areItemStackTagsEqual(current, stack);
            if (!sameItem) {
                return slot;
            }
            if (stack.isDamageable() && stack.getDamage() != current.getDamage()) {
                return slot;
            }
        }

        return -1;
    }

    private boolean isChestplate(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ArmorItem armor)) {
            return false;
        }
        return armor.getEquipmentSlot() == EquipmentSlotType.CHEST;
    }

    private double scoreChestplate(ItemStack stack) {
        if (!(stack.getItem() instanceof ArmorItem armor)) {
            return -1;
        }
        double base = armor.getDamageReduceAmount();
        base += EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack) * 0.35;
        base += EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, stack) * 0.15;
        if (stack.isDamageable()) {
            base -= (double) stack.getDamage() / Math.max(1, stack.getMaxDamage());
        }
        return base;
    }

    private int toContainerSlot(int inventorySlot) {
        return inventorySlot < 9 ? inventorySlot + 36 : inventorySlot;
    }

    private void scheduleInventoryAction(long delay, Runnable task) {
        InventoryUtility.sheduler.schedule(() -> {
            if (mc.player == null || mc.player.inventory == null || !isState()) {
                swapping = false;
                return;
            }
            task.run();
        }, delay, TimeUnit.MILLISECONDS);
    }
}
