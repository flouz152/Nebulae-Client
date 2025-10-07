package beame.components.modules.combat;

import beame.module.Category;
import beame.module.Module;
import beame.util.math.TimerUtil;
import events.Event;
import events.impl.player.EventUpdate;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class ECFold extends Module {

    private enum Phase {
        IDLE,
        WAIT_CHEST,
        STORE_ITEMS,
        CLOSE_CHEST,
        REMOVE_ARMOR,
        FINISHED
    }

    private final TimerUtil commandTimer = new TimerUtil();
    private final TimerUtil clickTimer = new TimerUtil();
    private Phase phase = Phase.IDLE;
    private boolean storingArmor;
    private int armorIndex;
    private long nextClickDelay = 90L;

    public ECFold() {
        super("ECFold", Category.Combat, true, "Складывает вещи в эндер-сундук");
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        if (mc.player == null) {
            setState(false);
            return;
        }
        storingArmor = false;
        armorIndex = 0;
        phase = Phase.WAIT_CHEST;
        commandTimer.setMs(250L);
        clickTimer.reset();
        sendOpenCommand();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        phase = Phase.IDLE;
        storingArmor = false;
        armorIndex = 0;
    }

    @Override
    public void event(Event event) {
        if (!(event instanceof EventUpdate) || mc.player == null) {
            return;
        }

        switch (phase) {
            case WAIT_CHEST -> handleWaitChest();
            case STORE_ITEMS -> handleStore();
            case CLOSE_CHEST -> handleCloseChest();
            case REMOVE_ARMOR -> handleArmorRemoval();
            case FINISHED -> setState(false);
            default -> {}
        }
    }

    private void handleWaitChest() {
        if (isEnderChestOpen()) {
            phase = Phase.STORE_ITEMS;
            clickTimer.reset();
            nextClickDelay = randomDelay();
            return;
        }
        if (commandTimer.hasReached(1200)) {
            sendOpenCommand();
        }
    }

    private void handleStore() {
        if (!isEnderChestOpen()) {
            if (commandTimer.hasReached(600)) {
                sendOpenCommand();
            }
            return;
        }

        ChestContainer container = (ChestContainer) mc.player.openContainer;
        if (!dumpInventory(container)) {
            phase = Phase.CLOSE_CHEST;
        }
    }

    private void handleCloseChest() {
        if (mc.currentScreen instanceof ChestScreen) {
            mc.player.closeScreen();
            clickTimer.reset();
            nextClickDelay = randomDelay();
            return;
        }
        if (mc.currentScreen == null) {
            if (storingArmor) {
                phase = Phase.FINISHED;
            } else {
                phase = Phase.REMOVE_ARMOR;
                clickTimer.reset();
                nextClickDelay = randomDelay();
            }
        }
    }

    private void handleArmorRemoval() {
        if (!clickTimer.hasReached(nextClickDelay)) {
            return;
        }

        int[] armorSlots = {8, 7, 6, 5};
        if (armorIndex >= armorSlots.length) {
            storingArmor = true;
            armorIndex = 0;
            phase = Phase.WAIT_CHEST;
            commandTimer.setMs(220L);
            sendOpenCommand();
            return;
        }

        int slotId = armorSlots[armorIndex];
        ItemStack stack = mc.player.container.getSlot(slotId).getStack();
        if (!stack.isEmpty()) {
            mc.playerController.windowClick(0, slotId, 0, ClickType.QUICK_MOVE, mc.player);
        }
        armorIndex++;
        clickTimer.reset();
        nextClickDelay = randomDelay();
    }

    private boolean dumpInventory(ChestContainer container) {
        if (!clickTimer.hasReached(nextClickDelay)) {
            return true;
        }
        if (!mc.player.inventory.getItemStack().isEmpty()) {
            placeCarriedStack(container);
            return true;
        }
        int start = container.getNumRows() * 9;
        for (int i = start; i < container.inventorySlots.size(); i++) {
            Slot slot = container.inventorySlots.get(i);
            if (slot == null || !slot.getHasStack()) {
                continue;
            }
            mc.playerController.windowClick(container.windowId, slot.slotNumber, 0, ClickType.QUICK_MOVE, mc.player);
            clickTimer.reset();
            nextClickDelay = randomDelay();
            return true;
        }
        return false;
    }

    private void sendOpenCommand() {
        if (mc.player == null) {
            return;
        }
        if (!commandTimer.hasReached(200)) {
            return;
        }
        mc.player.sendChatMessage("/ec open");
        commandTimer.reset();
    }

    private boolean isEnderChestOpen() {
        if (!(mc.player.openContainer instanceof ChestContainer container)) {
            return false;
        }
        IInventory lower = container.getLowerChestInventory();
        if (lower == mc.player.getInventoryEnderChest()) {
            return true;
        }
        if (mc.currentScreen instanceof ChestScreen screen) {
            String title = screen.getTitle().getString().toLowerCase(Locale.ROOT);
            return title.contains("ender") || title.contains("эндер");
        }
        return false;
    }

    private long randomDelay() {
        return ThreadLocalRandom.current().nextLong(65L, 125L);
    }

    private void placeCarriedStack(ChestContainer container) {
        int chestSlotCount = container.getNumRows() * 9;
        for (int i = 0; i < chestSlotCount; i++) {
            Slot chestSlot = container.inventorySlots.get(i);
            if (chestSlot != null && !chestSlot.getHasStack()) {
                mc.playerController.windowClick(container.windowId, chestSlot.slotNumber, 0, ClickType.PICKUP, mc.player);
                clickTimer.reset();
                nextClickDelay = randomDelay();
                return;
            }
        }
        int fallbackIndex = chestSlotCount;
        if (fallbackIndex < container.inventorySlots.size()) {
            Slot fallback = container.inventorySlots.get(fallbackIndex);
            mc.playerController.windowClick(container.windowId, fallback.slotNumber, 0, ClickType.PICKUP, mc.player);
        }
        clickTimer.reset();
        nextClickDelay = randomDelay();
    }
}
