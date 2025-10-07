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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LDSwap extends Module {

    private final BindSetting swapBind = new BindSetting("Кнопка свапа", 0);
    private final TimerUtil swapTimer = new TimerUtil();
    private boolean queued;
    private boolean swapping;
    private int lastInventorySlot = -1;

    public LDSwap() {
        super("LDSwap", Category.Combat, true, "Меняет нагрудник с обходом");
        addSettings(swapBind);
        swapTimer.setMs(0);
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
        if (mc.player == null || mc.world == null || !swapTimer.hasReached(250)) {
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

        new Thread(() -> {
            try {
                int chestSlot = 6;
                int delayBase = ThreadLocalRandom.current().nextInt(40, 80);
                if (hadChestplateEquipped) {
                    InventoryUtility.pickupItem(chestSlot, 0);
                    Thread.sleep(delayBase);
                }
                InventoryUtility.pickupItem(containerSlot, 0);
                Thread.sleep(ThreadLocalRandom.current().nextInt(45, 90));
                InventoryUtility.pickupItem(chestSlot, 0);
                Thread.sleep(ThreadLocalRandom.current().nextInt(30, 60));
                if (!mc.player.inventory.getItemStack().isEmpty()) {
                    InventoryUtility.pickupItem(containerSlot, 0);
                }
                mc.player.swingArm(Hand.MAIN_HAND);
                lastInventorySlot = replacementSlot;
            } catch (InterruptedException ignored) {
            } finally {
                swapping = false;
                swapTimer.reset();
            }
        }, "ldswap-thread").start();
    }

    private int findReplacementSlot(ItemStack current) {
        List<Integer> candidates = IntStream.range(0, 36)
                .filter(i -> isChestplate(mc.player.inventory.getStackInSlot(i)))
                .boxed()
                .sorted(Comparator.comparingDouble((Integer slot) -> -scoreChestplate(mc.player.inventory.getStackInSlot(slot))))
                .collect(Collectors.toList());

        for (int slot : candidates) {
            if (slot == lastInventorySlot) {
                continue;
            }
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

        if (!candidates.isEmpty()) {
            return candidates.get(0);
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
}
