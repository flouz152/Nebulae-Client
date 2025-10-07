package beame.components.modules.misc.AutoBuyLogic.Items;


import net.minecraft.item.ItemStack;

import java.time.LocalDateTime;

public class BuyedItem {
// leaked by itskekoff; discord.gg/sk3d geNpDP4s
    public BuyedItem(ItemStack ahItem, ItemStack parsedItem, int price, int count, AutoBuyItemClass abItem, boolean buyed, LocalDateTime buyTime) {
        this.ahItem = ahItem;
        this.parsedItem = parsedItem;
        this.price = price;
        this.count = count;
        this.abItem = abItem;
        this.buyed = buyed;
        this.buyTime = buyTime;
    }

    public ItemStack ahItem;
    public ItemStack parsedItem;
    public int price;
    public int count;
    public AutoBuyItemClass abItem;
    public boolean buyed;
    public LocalDateTime buyTime;
    public boolean sold = false;
}
