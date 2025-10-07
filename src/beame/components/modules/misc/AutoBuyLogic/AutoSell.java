package beame.components.modules.misc.AutoBuyLogic;

import beame.Nebulae;
import beame.components.modules.misc.AutoBuyLogic.Items.BuyedItem;
import beame.util.player.InventoryUtility;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CHeldItemChangePacket;

import static beame.util.IMinecraft.mc;

public class AutoSell {
// leaked by itskekoff; discord.gg/sk3d jqLNdVgb
    private boolean autoSellBusy = false;
    private BuyedItem currentSellItem = null;
    private long lastSellAction = 0;
    private boolean waitingForStorage = false;
    private long lastStorageCheck = 0;

    public void onUpdate() {
        if (!Nebulae.getHandler().getModuleList().autoBuy.autoSell.get()) return;
        if (waitingForStorage) {
            if (System.currentTimeMillis() > lastSellAction && beame.components.modules.misc.AutoBuyLogic.AutoBuyUtil.isAuctionOpened()) {
                InventoryUtility.clickSlotId(46, 0, ClickType.PICKUP, false);
                lastSellAction = System.currentTimeMillis() + 50;
            } else if (System.currentTimeMillis() > lastSellAction && mc.player.openContainer != null && mc.player.openContainer.getSlot(0).getHasStack()) {
                InventoryUtility.clickSlotId(0, 0, ClickType.PICKUP, false);
                lastSellAction = System.currentTimeMillis() + 50;
            } else if (System.currentTimeMillis() > lastSellAction && mc.player.openContainer != null && !mc.player.openContainer.getSlot(0).getHasStack()) {
                waitingForStorage = false;
                mc.player.closeScreen();
                mc.player.sendChatMessage("/ah");
                lastSellAction = System.currentTimeMillis() + 200;
            }
            return;
        }
        if (autoSellBusy) return;
        BuyedItem toSell = null;
        synchronized (Nebulae.getHandler().autoBuy.rlyBuyedItems) {
            for (BuyedItem item : Nebulae.getHandler().autoBuy.rlyBuyedItems) {
                if (item.buyed && !item.sold) {
                    toSell = item;
                    break;
                }
            }
        }
        if (toSell == null) return;
        int slot = InventoryUtility.getItemInHotBar(toSell.ahItem.getItem());
        if (slot == -1) return;
        if (mc.player.inventory.currentItem != slot) {
            mc.player.inventory.currentItem = slot;
            mc.player.connection.sendPacket(new CHeldItemChangePacket(slot));
            return;
        }
        int price = (int) (toSell.price * (1 + Nebulae.getHandler().getModuleList().autoBuy.autoSellPercent.get() / 100.0));
        mc.player.sendChatMessage("/ah sell " + price);
        autoSellBusy = true;
        currentSellItem = toSell;
        lastSellAction = System.currentTimeMillis() + 500;
    }

    public void onChatMessage(String text) {
        if (!Nebulae.getHandler().getModuleList().autoBuy.autoSell.get()) return;
        if (text.contains("Освободите хранилище или уберите предметы с продажи")) {
            waitingForStorage = true;
            lastStorageCheck = System.currentTimeMillis();
            mc.player.sendChatMessage("/ah");
            lastSellAction = System.currentTimeMillis() + 100;
        }
        if (text.contains("успешно выставили предмет на аукцион") || text.contains("Выставлен лот")) {
            if (currentSellItem != null) {
                currentSellItem.sold = true;
                autoSellBusy = false;
                currentSellItem = null;
            }
        }
    }

    public void reset() {
        autoSellBusy = false;
        currentSellItem = null;
        waitingForStorage = false;
        lastSellAction = 0;
        lastStorageCheck = 0;
    }
}
