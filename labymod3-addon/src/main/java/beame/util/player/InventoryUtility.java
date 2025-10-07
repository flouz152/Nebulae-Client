package beame.util.player;

import beame.Essence;
import beame.feature.notify.NotificationManager;
import beame.util.IMinecraft;
import beame.util.math.TimerUtil;
import events.impl.packet.EventPacket;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.IntStream;

public class InventoryUtility
        implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d WYcgMyrq
    public static Slot getAxeInInventory;
    private static InventoryUtility instance = new InventoryUtility();

    public static ScheduledExecutorService sheduler = new ScheduledThreadPoolExecutor(1);

    public static int findEmptySlot(boolean inHotBar) {
        int start = inHotBar ? 0 : 9;
        int end = inHotBar ? 9 : 45;
        for (int i = start; i < end; ++i) {
            if (!mc.player.inventory.getStackInSlot(i).isEmpty()) continue;
            return i;
        }
        return -1;
    }

    public static void swapHand(Slot slot, net.minecraft.util.Hand hand, boolean packet) {
        if (slot == null) return;

        if (hand == net.minecraft.util.Hand.MAIN_HAND) {
            int slotId = slot.slotNumber;
            if (slotId < 9) {
                mc.player.inventory.currentItem = slotId;
                if (packet) {
                    mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
                }
            } else {
                mc.playerController.windowClick(0, slotId, mc.player.inventory.currentItem, ClickType.SWAP, mc.player);
            }
        } else if (hand == net.minecraft.util.Hand.OFF_HAND) {
            int slotId = slot.slotNumber;
            if (slotId < 45) {

                if (slotId >= 36) {

                    mc.playerController.windowClick(0, slotId, 0, ClickType.PICKUP, mc.player);
                } else {

                    mc.playerController.windowClick(0, slotId, 0, ClickType.PICKUP, mc.player);
                }

                mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);

                if (packet) {
                    mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
                }
            }
        }
    }

    public static void dropItem(int slot) {
        mc.playerController.windowClick(0, slot, 0, ClickType.THROW, mc.player);
    }


    public static void pickupItem(int slot, int button) {
        mc.playerController.windowClick(0, slot, button, ClickType.PICKUP, mc.player);
    }

    public static int getChestplate() {
        for (int i = 0; i < 45; ++i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() instanceof ArmorItem)
                if (((ArmorItem) itemStack.getItem()).getEquipmentSlot() == EquipmentSlotType.CHEST)
                    return i == 40 ? 45 : i < 9 ? 36 + i : i;
        }
        return -1;
    }



    public static int getItemInHotBar(Item item) {
        return IntStream.range(0, 9).filter(i -> mc.player.inventory.getStackInSlot(i).getItem().equals(item)).findFirst().orElse(-1);
    }

    public  static void swapHands(int slotId, net.minecraft.util.Hand hand, boolean packet) {
        if (slotId == -1) return;
        int button = hand.equals(net.minecraft.util.Hand.MAIN_HAND) ? mc.player.inventory.currentItem : 40;
        clickSlotId(slotId, button, ClickType.SWAP, packet);
    }

    public static int findBlockInHotbar() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem)) continue;
            return i;
        }
        return -1;
    }

    public static int getHotbarSlotOfItem() {
        for (ItemStack stack : mc.player.getArmorInventoryList()) {
            if (stack.getItem() != Items.ELYTRA) continue;
            return -2;
        }
        int slot = -1;
        for (int i = 0; i < 36; ++i) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (s.getItem() != Items.ELYTRA) continue;
            slot = i;
            break;
        }
        if (slot < 9 && slot != -1) {
            slot += 36;
        }
        return slot;
    }

    public static int getSlotIDFromItem(Item item) {
        for (ItemStack stack : mc.player.getArmorInventoryList()) {
            if (stack.getItem() != item) continue;
            return -2;
        }
        int slot = -1;
        for (int i = 0; i < 36; ++i) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (s.getItem() != item) continue;
            slot = i;
            break;
        }
        if (slot < 9 && slot != -1) {
            slot += 36;
        }
        return slot;
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void moveItem(int from, int to) {
        if (from == to) {
            return;
        }
        clickSlot(from, 0, ClickType.SWAP);
        clickSlot(to, 0, ClickType.SWAP);
        clickSlot(from, 0, ClickType.SWAP);
    }
    private static InventoryUtility.Hand handUtil = new InventoryUtility.Hand();

    public static int findAndTrowItem(int hbSlot, int invSlot) {
        if (hbSlot != -1) {
            handUtil.setOriginalSlot(mc.player.inventory.currentItem);
            mc.player.connection.sendPacket(new CHeldItemChangePacket(hbSlot));
            return hbSlot;
        }
        if (invSlot != -1) {
            handUtil.setOriginalSlot(mc.player.inventory.currentItem);
            mc.playerController.pickItem(invSlot);
            return invSlot;
        }
        return -1;
    }



    public static void moveItem2(int from, int to, boolean air) {

        if (from == to) return;
        pickupItem(from, 0);
        pickupItem(to, 0);
        if (air)
            pickupItem(from, 0);
    }

    public static void clickSlot(int slot, int button, ClickType type) {
        mc.playerController.windowClick(mc.player.openContainer.windowId, slot, button, type, mc.player);
    }

    public static int getAxeInInventory(boolean inHotBar) {
        int firstSlot = inHotBar ? 0 : 9;
        int lastSlot = inHotBar ? 9 : 36;
        for (int i = firstSlot; i < lastSlot; ++i) {
            if (!(mc.player.inventory.getStackInSlot(i).getItem() instanceof AxeItem)) continue;
            return i;
        }
        return -1;
    }

    public static int findBestSlotInHotBar() {
        int emptySlot = findEmptySlot();
        if (emptySlot != -1) {
            return emptySlot;
        }
        return findNonSwordSlot();
    }

    private static int findEmptySlot() {
        for (int i = 0; i < 9; ++i) {
            if (!mc.player.inventory.getStackInSlot(i).isEmpty() || mc.player.inventory.currentItem == i) continue;
            return i;
        }
        return -1;
    }

    private static int findNonSwordSlot() {
        for (int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() instanceof SwordItem || mc.player.inventory.getStackInSlot(i).getItem() instanceof ElytraItem || mc.player.inventory.currentItem == i) continue;
            return i;
        }
        return -1;
    }


    public int getSlotInInventory(Item item) {
        int finalSlot = -1;
        for (int i = 0; i < 36; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() != item) continue;
            finalSlot = i;
        }
        return finalSlot;
    }

    public Slot getInventorySlot(Item item) {
        return mc.player.openContainer.inventorySlots.stream().filter(s -> s.getStack().getItem().equals(item) && s.slotNumber >= mc.player.openContainer.inventorySlots.size() - 36).findFirst().orElse(null);
    }

    public Slot getInventorySlot(List<Item> item) {
        return mc.player.openContainer.inventorySlots.stream().filter(s -> item.contains(s.getStack().getItem()) && s.slotNumber >= mc.player.openContainer.inventorySlots.size() - 36).findFirst().orElse(null);
    }

    public Slot getFoodMaxSaturationSlot() {
        return mc.player.openContainer.inventorySlots.stream().filter(s -> s.getStack().getItem().getFood() != null && !s.getStack().getItem().getFood().canEatWhenFull()).max(Comparator.comparingDouble(s -> s.getStack().getItem().getFood().getSaturation())).orElse(null);
    }

    public static int getInventoryCount(Item item) {
        return IntStream.range(0, 45).filter(i -> mc.player.inventory.getStackInSlot(i).getItem().equals(item)).map(i -> mc.player.inventory.getStackInSlot(i).getCount()).sum();
    }

    public static void clickSlot(Slot slot, int button, ClickType clickType, boolean packet) {
        if (slot != null) clickSlotId(slot.slotNumber, button, clickType, packet);
    }

    public static void clickSlotId(int slot, int button, ClickType clickType, boolean packet) {
        if (packet) {
            mc.player.connection.sendPacket(new CClickWindowPacket(mc.player.openContainer.windowId, slot, button, clickType, ItemStack.EMPTY, mc.player.openContainer.getNextTransactionID(mc.player.inventory)));
        } else {
            mc.playerController.windowClick(mc.player.openContainer.windowId, slot, button, clickType, mc.player);
        }
    }

    public int getPrice(ItemStack itemStack) {
        CompoundNBT tag = itemStack.getTag();
        if (tag == null) return -1;
        String price = StringUtils.substringBetween(tag.toString(), "\"text\":\" $", "\"}]");
        if (price == null || price.isEmpty()) return -1;
        price = price.replaceAll(" ", "").replaceAll(",", "");
        return Integer.parseInt(price);
    }

    public int getSlotInInventoryOrHotbar(Item item, boolean inHotBar) {
        int firstSlot = inHotBar ? 0 : 9;
        int lastSlot = inHotBar ? 9 : 36;
        int finalSlot = -1;
        for (int i = firstSlot; i < lastSlot; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() != item) continue;
            finalSlot = i;
        }
        return finalSlot;
    }

    public static int getSlotInInventoryOrHotbar() {
        int firstSlot = 0;
        int lastSlot = 9;
        int finalSlot = -1;
        for (int i = firstSlot; i < lastSlot; ++i) {
            if (!(Block.getBlockFromItem(mc.player.inventory.getStackInSlot(i).getItem()) instanceof SlabBlock)) continue;
            finalSlot = i;
        }
        return finalSlot;
    }

    public static boolean doesHotbarHaveItem(Item item) {
        for (int i = 0; i < 9; ++i) {
            mc.player.inventory.getStackInSlot(i);
            if (mc.player.inventory.getStackInSlot(i).getItem() != item) continue;
            return true;
        }
        return false;
    }

    static void inventorySwapClick(Item item, int slsl, boolean rotation) {
        boolean curgui = Essence.getHandler().getModuleList().guiMove.isState();
        boolean shouldDisableMove = Essence.getHandler().getModuleList().guiMove.funtime.get();

        KeyBinding[] pressedKeys = {
                mc.gameSettings.keyBindForward,
                mc.gameSettings.keyBindBack,
                mc.gameSettings.keyBindLeft,
                mc.gameSettings.keyBindRight,
                mc.gameSettings.keyBindJump,
                mc.gameSettings.keyBindSprint,
                mc.gameSettings.keyBindSneak
        };

        for (KeyBinding keyBinding : pressedKeys) {
            keyBinding.setPressed(false);
        }

        if (shouldDisableMove) {
            Essence.getHandler().disableMove = true;
        }

        boolean wasSneaking = mc.player.isSneaking();
        mc.gameSettings.keyBindSneak.setPressed(false);
        if (mc.player.isSneaking()) {
            mc.player.setSneaking(false);
        }

        final TimerUtil timer = new TimerUtil();
        timer.reset();

        new Thread(() -> {
            while (!timer.hasReached(70)) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
                mc.gameSettings.keyBindSneak.setPressed(false);
            }

            if (slsl != -1) {
                if (doesHotbarHaveItem(item)) {
                    if (slsl != mc.player.inventory.currentItem) {
                        mc.player.connection.sendPacket(new CHeldItemChangePacket(slsl));
                    }
                    mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(net.minecraft.util.Hand.MAIN_HAND));
                    if (slsl == mc.player.inventory.currentItem) {
                        for (KeyBinding keyBinding : pressedKeys) {
                            boolean press = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
                            keyBinding.setPressed(press);
                        }
                        if(curgui) Essence.getHandler().getModuleList().guiMove.setState(true);
                        if (shouldDisableMove) {
                            Essence.getHandler().disableMove = false;
                        }
                        return;
                    }

                    timer.reset();
                    while (!timer.hasReached(5)) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                        }
                    }

                    mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                } else {
                    int swapSlot = 4;
                    if (mc.player.inventory.currentItem == 4) {
                        swapSlot = 5;
                    }

                    mc.playerController.windowClick(0, slsl, swapSlot, ClickType.SWAP, mc.player);
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(swapSlot));
                    mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(net.minecraft.util.Hand.MAIN_HAND));

                    timer.reset();
                    while (!timer.hasReached(5)) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                        }
                    }

                    mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                    mc.playerController.windowClick(0, slsl, swapSlot, ClickType.SWAP, mc.player);
                    mc.playerController.windowClick(0, slsl, swapSlot, ClickType.QUICK_MOVE, mc.player);
                }
            }

            timer.reset();
            while (!timer.hasReached(100)) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }

            for (KeyBinding keyBinding : pressedKeys) {
                boolean press = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
                keyBinding.setPressed(press);
            }
            if(curgui) Essence.getHandler().getModuleList().guiMove.setState(true);
            if (shouldDisableMove) {
                Essence.getHandler().disableMove = false;
            }

            if (wasSneaking && !mc.gameSettings.keyBindSneak.isPressed()) {
            }
        }).start();
    }


    public static boolean swapAndUse(Item item) {
        int slotId = getSlotIDFromItem(item);
        if (slotId == -1) {
            return false;
        }

        boolean curgui = Essence.getHandler().getModuleList().guiMove.isState();
        boolean shouldDisableMove = Essence.getHandler().getModuleList().guiMove.funtime.get();

        KeyBinding[] pressedKeys = {
                mc.gameSettings.keyBindForward,
                mc.gameSettings.keyBindBack,
                mc.gameSettings.keyBindLeft,
                mc.gameSettings.keyBindRight,
                mc.gameSettings.keyBindJump,
                mc.gameSettings.keyBindSprint,
                mc.gameSettings.keyBindSneak
        };

        if (shouldDisableMove) {
            Essence.getHandler().disableMove = true;
            for (KeyBinding keyBinding : pressedKeys) {
                keyBinding.setPressed(false);
            }
        }

        boolean wasSneaking = mc.player.isSneaking();
        mc.gameSettings.keyBindSneak.setPressed(false);
        if (mc.player.isSneaking()) {
            mc.player.setSneaking(false);
        }

        final TimerUtil timer = new TimerUtil();
        timer.reset();

        new Thread(() -> {
            while (!timer.hasReached(70)) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
                mc.gameSettings.keyBindSneak.setPressed(false);
            }

            if (slotId < 9) {
                int oldSlot = mc.player.inventory.currentItem;
                mc.player.connection.sendPacket(new CHeldItemChangePacket(slotId));
                mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(net.minecraft.util.Hand.MAIN_HAND));

                timer.reset();
                while (!timer.hasReached(10)) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                    }
                    mc.gameSettings.keyBindSneak.setPressed(false);
                }
                mc.player.connection.sendPacket(new CHeldItemChangePacket(oldSlot));
            } else {
                int swapSlot = 4;
                if (mc.player.inventory.currentItem == 4) {
                    swapSlot = 5;
                }
                mc.playerController.windowClick(0, slotId, swapSlot, ClickType.SWAP, mc.player);

                timer.reset();
                while (!timer.hasReached(10)) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                    }
                    mc.gameSettings.keyBindSneak.setPressed(false);
                }

                mc.player.connection.sendPacket(new CHeldItemChangePacket(swapSlot));
                mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(net.minecraft.util.Hand.MAIN_HAND));

                timer.reset();
                while (!timer.hasReached(10)) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                    }
                    mc.gameSettings.keyBindSneak.setPressed(false);
                }
                mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));

                timer.reset();
                while (!timer.hasReached(10)) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                    }
                    mc.gameSettings.keyBindSneak.setPressed(false);
                }
                mc.playerController.windowClick(0, slotId, swapSlot, ClickType.SWAP, mc.player);
                mc.playerController.windowClick(0, slotId, swapSlot, ClickType.QUICK_MOVE, mc.player);
            }

            timer.reset();
            while (!timer.hasReached(20)) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
                mc.gameSettings.keyBindSneak.setPressed(false);
            }

            if (wasSneaking && !mc.gameSettings.keyBindSneak.isPressed()) {
            }

            if(curgui) Essence.getHandler().getModuleList().guiMove.setState(true);
            if (shouldDisableMove) {
                Essence.getHandler().disableMove = false;
            }
        }).start();

        return true;
    }

    public static int findItemNoChanges(final int endSlot, final Item item) {
        for (int i = 0; i < endSlot; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }


    public static boolean inventorySwapClick(Item item, String nbt, String nbtReq, boolean rotation) {
        int slsl = -1;
        boolean found = false;


        for (int i = 0; i < 9; ++i) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (s.getItem() != item) continue;
            CompoundNBT tag = s.getTag();
            if(tag != null) {
                INBT nbtElement = tag.get(nbt);
                if(nbtElement != null && nbtElement.getString().equals(nbtReq)) {
                    found = true;
                    slsl = i;
                    break;
                }
            }
        }


        if (!found) {
            for (int i = 9; i < 36; ++i) {
                ItemStack s = mc.player.inventory.getStackInSlot(i);
                if (s.getItem() != item) continue;
                CompoundNBT tag = s.getTag();
                if(tag != null) {
                    INBT nbtElement = tag.get(nbt);
                    if(nbtElement != null && nbtElement.getString().equals(nbtReq)) {
                        found = true;
                        slsl = i;
                        break;
                    }
                }
            }
        }


        if (!found) {
            String itemName = "";
            switch(nbtReq) {
                case "ender_eye": itemName = "Дезориентация"; break;
                case "netherite_scrap": itemName = "Трапка"; break;
                case "dried_kelp": itemName = "Пласт"; break;
                case "sugar": itemName = "Явная пыль"; break;
                case "phantom_membrane": itemName = "Божья аура"; break;
                case "potion-acid": itemName = "Серная кислота"; break;
                case "potion-burp": itemName = "Зелье отрыжки"; break;
                case "potion-killer": itemName = "Зелье Киллера"; break;
                case "potion-medic": itemName = "Зелье Медика"; break;
                case "potion-winner": itemName = "Зелье Победителя"; break;
                case "potion-agent": itemName = "Зелье Агента"; break;
            }


            for (int i = 0; i < 9; ++i) {
                ItemStack s = mc.player.inventory.getStackInSlot(i);
                if (s.getItem() != item) continue;
                if (s.hasDisplayName() && s.getDisplayName().getString().contains(itemName)) {
                    found = true;
                    slsl = i;
                    break;
                }
            }


            if (!found) {
                for (int i = 9; i < 36; ++i) {
                    ItemStack s = mc.player.inventory.getStackInSlot(i);
                    if (s.getItem() != item) continue;
                    if (s.hasDisplayName() && s.getDisplayName().getString().contains(itemName)) {
                        found = true;
                        slsl = i;
                        break;
                    }
                }
            }
        }

        if(!found) {
            Essence.getHandler().notificationManager.pushNotify("Нет предмета для использования!", NotificationManager.Type.Info);
            return false;
        }

        inventorySwapClick(item, slsl, rotation);
        return true;
    }

    public static void inventorySwapClick(Item item, boolean rotation) {

        int slsl = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (s.getItem() != item) continue;
            slsl = i;
            break;
        }


        if (slsl == -1) {
            slsl = getItemSlot(item);
        }

        inventorySwapClick(item, slsl, rotation);
    }

    public static int getItemSlot(Item input) {
        for (ItemStack stack : mc.player.getArmorInventoryList()) {
            if (stack.getItem() != input) continue;
            return -2;
        }
        int slot = -1;

        for (int i = 9; i < 36; ++i) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (s.getItem() != input) continue;
            slot = i;
            break;
        }

        if (slot == -1) {
            for (int i = 0; i < 9; ++i) {
                ItemStack s = mc.player.inventory.getStackInSlot(i);
                if (s.getItem() != input) continue;
                slot = i;
                break;
            }
        }
        return slot;
    }

    public static int findNullSlot() {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof AirItem) {
                if (i < 9) {
                    i += 36;
                }
                return i;
            }
        }
        return 999;
    }

    public static InventoryUtility getInstance() {
        return instance;
    }

    public static class Hand {
        public static boolean isEnabled;
        private boolean isChangingItem;
        private int originalSlot = -1;

        public void onEventPacket(EventPacket eventPacket) {
            if (!eventPacket.isReceivePacket()) {
                return;
            }

            if (eventPacket.getPacket() instanceof SHeldItemChangePacket) {
                this.isChangingItem = true;
            }
        }

        public void handleItemChange(boolean resetItem) {
            if (this.isChangingItem && this.originalSlot != -1) {
                isEnabled = true;
                mc.player.inventory.currentItem = this.originalSlot;
                if (resetItem) {
                    this.isChangingItem = false;
                    this.originalSlot = -1;
                    isEnabled = false;
                }
            }
        }

        public void setOriginalSlot(int slot) {
            this.originalSlot = slot;
        }
    }
}