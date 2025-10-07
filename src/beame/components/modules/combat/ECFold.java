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
        OPENING_MAIN,
        STORE_MAIN,
        CLOSE_AFTER_MAIN,
        REMOVE_ARMOR,
        OPENING_ARMOR,
        STORE_ARMOR,
        FINISHED
    }

    private static final int[] ARMOR_SLOTS = {8, 7, 6, 5};

    private final TimerUtil commandTimer = new TimerUtil();
    private final TimerUtil clickTimer = new TimerUtil();

    private Phase phase = Phase.IDLE;
    private int armorIndex;
    private long nextClickDelay;

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
        armorIndex = 0;
        phase = Phase.OPENING_MAIN;
        commandTimer.reset();
        clickTimer.reset();
        scheduleNextClick();
        sendOpenCommand();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        phase = Phase.IDLE;
        armorIndex = 0;
    }

    @Override
    public void event(Event event) {
        if (!(event instanceof EventUpdate) || mc.player == null) {
            return;
        }

        switch (phase) {
            case OPENING_MAIN -> handleOpeningMain();
            case STORE_MAIN -> handleStoreMain();
            case CLOSE_AFTER_MAIN -> handleCloseAfterMain();
            case REMOVE_ARMOR -> handleRemoveArmor();
            case OPENING_ARMOR -> handleOpeningArmor();
            case STORE_ARMOR -> handleStoreArmor();
            case FINISHED -> setState(false);
            default -> {}
        }
    }

    private void handleOpeningMain() {
        if (isEnderChestOpen()) {
            phase = Phase.STORE_MAIN;
            scheduleNextClick();
            return;
        }
        if (commandTimer.hasReached(600L)) {
            sendOpenCommand();
        }
    }

    private void handleStoreMain() {
        ChestContainer container = getOpenEnderChest();
        if (container == null) {
            phase = Phase.OPENING_MAIN;
            return;
        }
        if (dumpInventory(container)) {
            return;
        }
        phase = Phase.CLOSE_AFTER_MAIN;
    }

    private void handleCloseAfterMain() {
        if (mc.currentScreen instanceof ChestScreen) {
            mc.player.closeScreen();
            clickTimer.reset();
            scheduleNextClick();
            return;
        }
        if (mc.currentScreen == null) {
            phase = Phase.REMOVE_ARMOR;
            armorIndex = 0;
            clickTimer.reset();
            scheduleNextClick();
        }
    }

    private void handleRemoveArmor() {
        if (!clickTimer.hasReached(nextClickDelay)) {
            return;
        }
        while (armorIndex < ARMOR_SLOTS.length) {
            int slotId = ARMOR_SLOTS[armorIndex++];
            ItemStack stack = mc.player.container.getSlot(slotId).getStack();
            if (!stack.isEmpty()) {
                mc.playerController.windowClick(mc.player.container.windowId, slotId, 0, ClickType.QUICK_MOVE, mc.player);
                scheduleNextClick();
                return;
            }
        }
        phase = Phase.OPENING_ARMOR;
        commandTimer.reset();
        sendOpenCommand();
    }

    private void handleOpeningArmor() {
        if (isEnderChestOpen()) {
            phase = Phase.STORE_ARMOR;
            scheduleNextClick();
            return;
        }
        if (commandTimer.hasReached(600L)) {
            sendOpenCommand();
        }
    }

    private void handleStoreArmor() {
        ChestContainer container = getOpenEnderChest();
        if (container == null) {
            phase = Phase.OPENING_ARMOR;
            return;
        }
        if (dumpInventory(container)) {
            return;
        }
        phase = Phase.FINISHED;
    }

    private boolean dumpInventory(ChestContainer container) {
        if (!clickTimer.hasReached(nextClickDelay)) {
            return true;
        }
        if (!mc.player.inventory.getItemStack().isEmpty()) {
            placeCarriedStack(container);
            scheduleNextClick();
            return true;
        }
        int start = container.getNumRows() * 9;
        for (int i = start; i < container.inventorySlots.size(); i++) {
            Slot slot = container.inventorySlots.get(i);
            if (slot == null || !slot.getHasStack()) {
                continue;
            }
            mc.playerController.windowClick(container.windowId, slot.slotNumber, 0, ClickType.QUICK_MOVE, mc.player);
            scheduleNextClick();
            return true;
        }
        return false;
    }

    private void placeCarriedStack(ChestContainer container) {
        int chestSlots = container.getNumRows() * 9;
        for (int i = 0; i < chestSlots; i++) {
            Slot slot = container.inventorySlots.get(i);
            if (slot != null && !slot.getHasStack()) {
                mc.playerController.windowClick(container.windowId, slot.slotNumber, 0, ClickType.PICKUP, mc.player);
                return;
            }
        }
        int fallbackIndex = chestSlots;
        if (fallbackIndex < container.inventorySlots.size()) {
            Slot fallback = container.inventorySlots.get(fallbackIndex);
            mc.playerController.windowClick(container.windowId, fallback.slotNumber, 0, ClickType.PICKUP, mc.player);
        }
    }

    private void sendOpenCommand() {
        if (mc.player == null) {
            return;
        }
        if (!commandTimer.hasReached(180L)) {
            return;
        }
        mc.player.sendChatMessage("/ec open");
        commandTimer.reset();
    }

    private ChestContainer getOpenEnderChest() {
        if (mc.player.openContainer instanceof ChestContainer container && isEnderChest(container)) {
            return container;
        }
        return null;
    }

    private boolean isEnderChestOpen() {
        return getOpenEnderChest() != null;
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

    private void scheduleNextClick() {
        nextClickDelay = ThreadLocalRandom.current().nextLong(55L, 110L);
        clickTimer.reset();
    }
}
