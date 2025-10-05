package beame.util.autobuy;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;

import java.util.*;

import static beame.util.IMinecraft.mc;

public class AuctionUtil {
// leaked by itskekoff; discord.gg/sk3d FPAmyy7n
    static HashMap<String, String> sphereHashMap = new HashMap<>(Map.ofEntries(
            Map.entry("sphere-himeri", "Химера"),
            Map.entry("sphere-apollona", "Аполлона"),
            Map.entry("sphere-andromeda", "Андромеда"),
            Map.entry("sphere-astreya", "Астрея"),
            Map.entry("sphere-pandora", "Пандоры"),
            Map.entry("sphere-titana", "Титана"),
            Map.entry("sphere-osirisa", "Осириса")
    ));

    public static int getSphereLVL(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if(tag == null)
            return -1;

        try {
            return Integer.parseInt(tag.get("tslevel").getString());
        } catch (Exception ex) {
            return -1;
        }
    }

    public static String getSphereName(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if(tag == null)
            return "";

        String name = "";
        try {
            name = tag.get("don-item").getString();
        } catch (Exception ex) {
            return "";
        }

        if(!sphereHashMap.containsKey(name))
            return "";
        return sphereHashMap.get(name);
    }

    public static int getPrice(ItemStack stack) {
        CompoundNBT display;
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("display", 10) && (display = tag.getCompound("display")).contains("Lore", 9)) {
            ListNBT lore = display.getList("Lore", 8);
            for (int j = 0; j < lore.size(); ++j) {
                JsonObject title;
                JsonArray array;
                JsonObject object = new JsonParser().parse(lore.getString(j)).getAsJsonObject();
                if (!object.has("extra") || (array = object.getAsJsonArray("extra")).size() <= 2 || !(title = array.get(1).getAsJsonObject()).get("text").getAsString().trim().toLowerCase().contains("\u0446\u0435\u043da"))
                    continue;
                String line = array.get(2).getAsJsonObject().get("text").getAsString().trim().substring(1).replaceAll(" ", "");
                return Integer.parseInt(line);
            }
        }
        return -1;
    }

    public static List<ITextComponent> getTooltip(ItemStack stack) {
        return stack.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
    }

    public static List<ItemStack> getShulkerBoxItems(ItemStack stack) {
        List<ItemStack> items = new ArrayList<>();
        boolean flag = stack.getItem() instanceof BlockItem && ((BlockItem)stack.getItem()).getBlock() instanceof ShulkerBoxBlock;
        if (flag && stack.hasTag()) {
            assert stack.getTag() != null;

            CompoundNBT blockEntityTag = stack.getTag().getCompound("BlockEntityTag");
            if (!blockEntityTag.contains("Items", 9)) {
                return items;
            } else {
                ListNBT itemsList = blockEntityTag.getList("Items", 10);

                for(int i = 0; i < itemsList.size(); ++i) {
                    CompoundNBT itemNBT = itemsList.getCompound(i);
                    ItemStack itemStack = ItemStack.read(itemNBT);
                    items.add(itemStack);
                }
            }
        }

        return items;
    }

    public static int indexOf(List<ITextComponent> t, String n) {
        Iterator var2 = t.iterator();

        ITextComponent text;
        do {
            if (!var2.hasNext()) {
                return -1;
            }

            text = (ITextComponent)var2.next();
        } while(!text.getString().equalsIgnoreCase(n));

        return t.indexOf(text);
    }
}
