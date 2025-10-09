package beame.components.modules.misc.AutoBuyLogic;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import java.util.List;
import beame.components.modules.misc.AutoBuyLogic.Items.Enchant;
import beame.components.modules.misc.AutoBuyLogic.Items.PotionEffectMatcher;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AutoBuyItemData {
// leaked by itskekoff; discord.gg/sk3d 32Klyios
    public String itemId;
    public int buyPrice;
    public String spookyItemType;
    public boolean isParsingEnabled;
    public List<Enchant> enchants;
    public List<PotionEffectMatcher> potionEffects;

    public AutoBuyItemData() {}

    public AutoBuyItemData(Item item, int buyPrice, String spookyItemType) {
        this.itemId = Registry.ITEM.getKey(item).toString();
        this.buyPrice = buyPrice;
        this.spookyItemType = spookyItemType;
        this.isParsingEnabled = false;
    }

    public AutoBuyItemData(Item item, int buyPrice, String spookyItemType, boolean isParsingEnabled) {
        this.itemId = Registry.ITEM.getKey(item).toString();
        this.buyPrice = buyPrice;
        this.spookyItemType = spookyItemType;
        this.isParsingEnabled = isParsingEnabled;
    }

    public AutoBuyItemData(Item item, int buyPrice, String spookyItemType, boolean isParsingEnabled, List<Enchant> enchants, List<PotionEffectMatcher> potionEffects) {
        this.itemId = Registry.ITEM.getKey(item).toString();
        this.buyPrice = buyPrice;
        this.spookyItemType = spookyItemType;
        this.isParsingEnabled = isParsingEnabled;
        this.enchants = enchants;
        this.potionEffects = potionEffects;
    }

    public Item getItem() {
        return Registry.ITEM.getOrDefault(new ResourceLocation(itemId));
    }
}
