package beame.components.modules.misc.AutoBuyLogic;


import beame.Essence;
import beame.util.ClientHelper;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.StringUtils;
import net.minecraft.potion.Potion;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static beame.util.IMinecraft.mc;

public class AutoBuyUtil {
// leaked by itskekoff; discord.gg/sk3d BCX87FQM
    public static String getSeller(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("display", 10)) {
            CompoundNBT display = tag.getCompound("display");
            if (display.contains("Lore", 9)) {
                ListNBT lore = display.getList("Lore", 8);

                for (int j = 0; j < lore.size(); ++j) {
                    JsonObject object = new JsonParser().parse(lore.getString(j)).getAsJsonObject();
                    if (object.has("extra")) {
                        JsonArray array = object.getAsJsonArray("extra");
                        if (array.size() > 2) {
                            JsonObject title = array.get(1).getAsJsonObject();
                            if (title.get("text").getAsString().trim().toLowerCase().contains("продавец")) {
                                return array.get(2).getAsJsonObject().get("text").getAsString().trim().substring(1);
                            }
                        }
                    }
                }
            }
        }

        return "";
    }

    public static int getPrice(ItemStack itemStack) {
        if (ClientHelper.isConnectedToServer("spookytime")) {
            if (!(mc.currentScreen instanceof ChestScreen)) {
                return -1;
            }

            ChestScreen screen = (ChestScreen) mc.currentScreen;
            String title = screen.getTitle().getString();

            if (!title.contains("Аукцион") && !title.contains("Поиск:")) {
                return -1;
            }

            List<ITextComponent> tooltip = itemStack.getTooltip(mc.player,
                    mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED
                            : ITooltipFlag.TooltipFlags.NORMAL);

            for (ITextComponent line : tooltip) {
                String text = line.getString();
                if (text.contains("Цена:") || text.contains("Price:")) {
                    try {
                        String priceStr = text.replaceAll("[^0-9]", "");
                        return Integer.parseInt(priceStr);
                    } catch (NumberFormatException e) {
                        return -1;
                    }
                }
            }
            return -1;
        } else {
            CompoundNBT tag = itemStack.getTag();
            if (tag == null) return -1;
            String price = StringUtils.substringBetween(tag.toString(), "\"text\":\" $", "\"}]");
            if (price == null || price.isEmpty()) return -1;
            price = price.replaceAll(" ", "").replaceAll(",", "");
            return Integer.parseInt(price);
        }
    }


    public static boolean isFake(ItemStack item, int buyPrice) {
        if(Essence.getHandler().autoBuyGUI.server == 0) return false;

        int itemPrice = getPrice(item);
        float badPriceFloat = buyPrice / 1.72f;
        int badPrice = (int) badPriceFloat;

        return !(itemPrice > badPrice);
    }

    public static int calculateDelay() {
        int pping = Essence.getHandler().autoBuy.curping;
        int ping = (pping == 0) ? 150 : Math.min(pping, 300);
        return (int)(ping * 1.5f);
    }

    public static HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> getAttributes(ItemStack stack) {
        HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> temp = new HashMap<>();
        Multimap<Attribute, AttributeModifier> itemStackAttributes = stack.getAttributeModifiers(EquipmentSlotType.OFFHAND);
        if(itemStackAttributes.isEmpty()) return null;

        for(Map.Entry<Attribute, AttributeModifier> entry : itemStackAttributes.entries()) {
            temp.put(entry.getKey(), Map.entry((float)entry.getValue().getAmount(), entry.getValue().getOperation()));
        }

        return temp;
    }

    public static boolean isAuctionOpened() {
        if(mc.player == null || mc.world == null) return false;

        boolean check1 = mc.currentScreen == null;
        if(check1) return false;

        String title = mc.currentScreen.getTitle().getString().toLowerCase();
        return title.contains("аукционы") || title.contains("поиск") || title.contains("подозрительная цена");
    }

    public static MessageType getMessageType(String message) {
        boolean ahMessage = message.startsWith("[☃]");
        if(!ahMessage) {
            if(message.equals("Не так быстро..")) return MessageType.Wait;
            return MessageType.No;
        }

        String msg = message.substring(4, message.length()-1);
        if(msg.startsWith("Вы успешно купили") && msg.contains(" за $")) {
            return MessageType.Buy;
        } else if(msg.contains("У Вас не хватает денег!")) {
            return MessageType.NoMoney;
        } else if(msg.contains("Этот товар уже купили!")) {
            return MessageType.Purchased;
        }

        return MessageType.No;
    }

    public static String extractBuyInformation(String message) {
        if(getMessageType(message) != MessageType.Buy) return "";

        String price = message.split("[$]")[1].replace("!", "");
        String name = message.replace("[☃] Вы успешно купили ", "").split(" за")[0];

        return price + "|" + name;
    }

    public static String getSpookyItemType(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        if (tag == null) return null;

        CompoundNBT publicBukkitValues = tag.getCompound("PublicBukkitValues");
        if (publicBukkitValues == null) return null;

        String stashId = publicBukkitValues.getString("spookystash:stash_id");
        String mythic = publicBukkitValues.getString("spookyevents:mythic");
        String type = publicBukkitValues.getString("spookyitems:spooky-item");
            String currency = publicBukkitValues.getString("spookystash:currency");
        if (stashId != null && !stashId.isEmpty()) {
            return "spookystash:stash_id:" + stashId;
        }
        if (mythic != null && !mythic.isEmpty()) {
            return "spookyevents:mythic:" + mythic;
        }
        if (type != null && !type.isEmpty()) {
            return type;
        }
            if (currency != null && currency.equals("soul")) {
                return "soul-currency";
            }
            return null;
    }

    public static List<EffectInstance> getPotionEffects(ItemStack stack) {
        return PotionUtils.getFullEffectsFromItem(stack);
        }


    public static int getSpookyItemLevel(ItemStack stack) {
        if (!stack.hasTag()) return -1;
        
        CompoundNBT tag = stack.getTag();
        if (tag.contains("PublicBukkitValues")) {
            CompoundNBT bukkit = tag.getCompound("PublicBukkitValues");
            if (bukkit.contains("spookyitems:sphere-item-level")) {
                return bukkit.getInt("spookyitems:sphere-item-level");
            }
        }
        return -1;
    }

    public enum MessageType {
        No,
        Buy,
        NoMoney,
        Purchased,
        Wait
    }
}
