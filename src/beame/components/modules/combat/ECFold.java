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
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class ECFold extends Module {

    private enum Phase {
        WAIT_MAIN_OPEN,
        STORE_MAIN,
        CLOSE_MAIN,
        STRIP_ARMOR,
        WAIT_ARMOR_OPEN,
        STORE_ARMOR,
        COMPLETE
    }

    private static final int[] ARMOR_SLOT_INDICES = {8, 7, 6, 5};

    private final TimerUtil actionTimer = new TimerUtil();
    private final TimerUtil commandTimer = new TimerUtil();

    private Phase phase = Phase.WAIT_MAIN_OPEN;
    private long nextActionDelay;
    private int armorIndex;

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
        phase = Phase.WAIT_MAIN_OPEN;
        armorIndex = 0;
        actionTimer.reset();
        commandTimer.reset();
        commandTimer.setMs(250L);
        scheduleNextAction();
        sendOpenCommand();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        phase = Phase.WAIT_MAIN_OPEN;
        armorIndex = 0;
    }

    @Override
    public void event(Event event) {
        if (!(event instanceof EventUpdate) || mc.player == null) {
            return;
        }

        switch (phase) {
            case WAIT_MAIN_OPEN -> waitForChest(false);
            case STORE_MAIN -> storeInventory(false);
            case CLOSE_MAIN -> closeChest();
            case STRIP_ARMOR -> stripArmor();
            case WAIT_ARMOR_OPEN -> waitForChest(true);
            case STORE_ARMOR -> storeInventory(true);
            case COMPLETE -> setState(false);
        }
    }

    private void waitForChest(boolean armorPhase) {
        ChestContainer container = getOpenEnderChest();
        if (container != null) {
            phase = armorPhase ? Phase.STORE_ARMOR : Phase.STORE_MAIN;
            actionTimer.reset();
            scheduleNextAction();
            return;
        }
        if (mc.currentScreen != null && !(mc.currentScreen instanceof ChestScreen)) {
            mc.player.closeScreen();
        }
        if (commandTimer.hasReached(350L)) {
            sendOpenCommand();
        }
    }

    private void storeInventory(boolean armorPhase) {
        ChestContainer container = getOpenEnderChest();
        if (container == null) {
            phase = armorPhase ? Phase.WAIT_ARMOR_OPEN : Phase.WAIT_MAIN_OPEN;
            commandTimer.reset();
            return;
        }
        if (!actionTimer.hasReached(nextActionDelay)) {
            return;
        }
        if (handleCarriedStack(container)) {
            scheduleNextAction();
            return;
        }
        if (transferOneItem(container)) {
            scheduleNextAction();
            return;
        }
        if (armorPhase) {
            phase = Phase.COMPLETE;
        } else {
            phase = Phase.CLOSE_MAIN;
        }
        actionTimer.reset();
        scheduleNextAction();
    }

    private void closeChest() {
        if (mc.currentScreen instanceof ChestScreen) {
            if (actionTimer.hasReached(nextActionDelay)) {
                mc.player.closeScreen();
                actionTimer.reset();
                scheduleNextAction();
            }
            return;
        }
        phase = Phase.STRIP_ARMOR;
        armorIndex = 0;
        actionTimer.reset();
        scheduleNextAction();
    }

    private void stripArmor() {
        if (mc.player == null || !actionTimer.hasReached(nextActionDelay)) {
            return;
        }
        while (armorIndex < ARMOR_SLOT_INDICES.length) {
            int slotId = ARMOR_SLOT_INDICES[armorIndex++];
            Container container = mc.player.container;
            if (container == null) {
                continue;
            }
            Slot slot = container.getSlot(slotId);
            if (slot != null && slot.getHasStack()) {
                mc.playerController.windowClick(container.windowId, slot.slotNumber, 0, ClickType.QUICK_MOVE, mc.player);
                scheduleNextAction();
                return;
            }
        }
        phase = Phase.WAIT_ARMOR_OPEN;
        commandTimer.reset();
        commandTimer.setMs(250L);
        sendOpenCommand();
        actionTimer.reset();
        scheduleNextAction();
    }

    private boolean transferOneItem(ChestContainer container) {
        int chestSlots = container.getNumRows() * 9;
        for (int i = chestSlots; i < container.inventorySlots.size(); i++) {
            Slot slot = container.inventorySlots.get(i);
            if (slot == null || !slot.getHasStack()) {
                continue;
            }
            mc.playerController.windowClick(container.windowId, slot.slotNumber, 0, ClickType.QUICK_MOVE, mc.player);
            return true;
        }
        return false;
    }

    private boolean handleCarriedStack(ChestContainer container) {
        ItemStack carried = mc.player.inventory.getItemStack();
        if (carried.isEmpty()) {
            return false;
        }
        int chestSlots = container.getNumRows() * 9;
        for (int i = 0; i < chestSlots; i++) {
            Slot slot = container.inventorySlots.get(i);
            if (slot != null && !slot.getHasStack()) {
                mc.playerController.windowClick(container.windowId, slot.slotNumber, 0, ClickType.PICKUP, mc.player);
                return true;
            }
        }
        int fallbackIndex = chestSlots;
        if (fallbackIndex < container.inventorySlots.size()) {
            Slot fallback = container.inventorySlots.get(fallbackIndex);
            mc.playerController.windowClick(container.windowId, fallback.slotNumber, 0, ClickType.PICKUP, mc.player);
            return true;
        }
        return false;
    }

    private ChestContainer getOpenEnderChest() {
        if (mc.player == null) {
            return null;
        }
        if (mc.player.openContainer instanceof ChestContainer chest && isEnderChest(chest)) {
            return chest;
        }
        return null;
    }

    private boolean isEnderChest(ChestContainer container) {
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

    private void sendOpenCommand() {
        if (mc.player == null) {
            return;
        }
        if (!commandTimer.hasReached(200L)) {
            return;
        }
        mc.player.sendChatMessage("/ec open");
        commandTimer.reset();
    }

    private void scheduleNextAction() {
        nextActionDelay = ThreadLocalRandom.current().nextLong(45L, 95L);
        actionTimer.reset();
    }
}
