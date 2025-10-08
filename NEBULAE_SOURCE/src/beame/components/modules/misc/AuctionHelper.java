package beame.components.modules.misc;

import beame.components.command.AbstractCommand;
import beame.components.modules.misc.AutoBuyLogic.Items.Enchant;
import beame.util.ClientHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import events.Event;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.text.ITextComponent;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.RadioSetting;
import beame.setting.SettingList.SliderSetting;

import java.text.NumberFormat;
import java.util.*;

public class AuctionHelper extends Module {
// leaked by itskekoff; discord.gg/sk3d vvi176sK
    public AuctionHelper() {
        super("AuctionHelper", Category.Misc, true, "Помощник в покупке предметов на аукционе");
        addSettings(filter, showNBT, showAttributes, showPricePerItem, filterSelect, filter_armor_protection5, filter_armor_pochinka, filter_armor_prochnost5, filter_armor_ship, filter_armor_podvodka3, filter_sword_sharpness7,
                filter_sword_knockback, filter_sword_looting, filter_sword_opitniy, opitniylvl, filter_sword_vampir, vampirlvl, filter_sword_okisl,okisllvl, filter_sword_yad,yadlvl,filter_hoe_prochnost5, filter_hoe_udacha5, filter_hoe_ozelenitel, filter_hoe_mending, filter_pickaxe_eff, efflvl, filter_pickaxe_udacha,
                udachalvl, filter_pickaxe_prochnost,procnhostlvl,filter_pickaxe_buldozer,buldozerlvl,filter_pickaxe_shelk,filter_pickaxe_mending);
    }

    public BooleanSetting filter = new BooleanSetting("Фильтр");
    public BooleanSetting showPricePerItem = new BooleanSetting("Цена за 1 предмет", true);
    public BooleanSetting showItemLore = new BooleanSetting("Показывать лор предметов", false);
    public BooleanSetting showNBT = new BooleanSetting("Показывать NBT предметов", false);
    public BooleanSetting showAttributes = new BooleanSetting("Показывать атрибуты предметов", false);

    public RadioSetting filterSelect = new RadioSetting("Настройки фильтра", "Броня", "Броня", "Меч", "Мотыга", "Кирка").setVisible(() -> filter.get());

    public BooleanSetting filter_armor_protection5 = new BooleanSetting("С защитой 5", true).setVisible(() -> filter.get() && filterSelect.getIndex() == 0);
    public BooleanSetting filter_armor_pochinka = new BooleanSetting("С починкой", true).setVisible(() -> filter.get() && filterSelect.getIndex() == 0);
    public BooleanSetting filter_armor_prochnost5 = new BooleanSetting("С прочностью 5", true).setVisible(() -> filter.get() && filterSelect.getIndex() == 0);
    public BooleanSetting filter_armor_ship = new BooleanSetting("С шипами", false).setVisible(() -> filter.get() && filterSelect.getIndex() == 0);
    public BooleanSetting filter_armor_podvodka3 = new BooleanSetting("Ботинки: С подводной ходьбой 3", true).setVisible(() -> filter.get() && filterSelect.getIndex() == 0);

    public BooleanSetting filter_sword_sharpness7 = new BooleanSetting("С остротой 7", true).setVisible(() -> filter.get() && filterSelect.getIndex() == 1);
    public BooleanSetting filter_sword_knockback = new BooleanSetting("С отдачей").setVisible(() -> filter.get() && filterSelect.getIndex() == 1);
    public BooleanSetting filter_sword_looting = new BooleanSetting("С добычей").setVisible(() -> filter.get() && filterSelect.getIndex() == 1);
    public BooleanSetting filter_sword_opitniy = new BooleanSetting("С опытным", true).setVisible(() -> filter.get() && filterSelect.getIndex() == 1);
    public SliderSetting opitniylvl = new SliderSetting("Уровень опытного", 3, 1, 3, 1).setVisible(() -> filter_sword_opitniy.get() && filterSelect.getIndex() == 1);
    public BooleanSetting filter_sword_vampir = new BooleanSetting("С вампиризмом", true).setVisible(() -> filter.get() && filterSelect.getIndex() == 1);
    public SliderSetting vampirlvl = new SliderSetting("Уровень вампиризм", 2, 1, 2, 1).setVisible(() -> filter_sword_vampir.get() && filterSelect.getIndex() == 1);
    public BooleanSetting filter_sword_okisl = new BooleanSetting("С окислением", true).setVisible(() -> filter.get() && filterSelect.getIndex() == 1);
    public SliderSetting okisllvl = new SliderSetting("Уровень окисления", 2, 1, 2, 1).setVisible(() -> filter_sword_okisl.get() && filterSelect.getIndex() == 1);
    public BooleanSetting filter_sword_yad = new BooleanSetting("С ядом", true).setVisible(() -> filter.get() && filterSelect.getIndex() == 1);
    public SliderSetting yadlvl = new SliderSetting("Уровень яда", 3, 1, 3, 1).setVisible(() -> filter_sword_yad.get() && filterSelect.getIndex() == 1);

    public BooleanSetting filter_hoe_prochnost5 = new BooleanSetting("С прочностью 5").setVisible(() -> filter.get() && filterSelect.getIndex() == 2);
    public BooleanSetting filter_hoe_udacha5 = new BooleanSetting("С удачей 5").setVisible(() -> filter.get() && filterSelect.getIndex() == 2);
    public BooleanSetting filter_hoe_ozelenitel = new BooleanSetting("С озеленителем 3").setVisible(() -> filter.get() && filterSelect.getIndex() == 2);
    public BooleanSetting filter_hoe_mending = new BooleanSetting("С починкой").setVisible(() -> filter.get() && filterSelect.getIndex() == 2);

    public BooleanSetting filter_pickaxe_eff = new BooleanSetting("С эффективностью", true).setVisible(() -> filter.get() && filterSelect.getIndex() == 3);
    public SliderSetting efflvl = new SliderSetting("Уровень эффективности", 6, 1, 7, 1).setVisible(() -> filter_pickaxe_eff.get() && filterSelect.getIndex() == 3);
    public BooleanSetting filter_pickaxe_udacha = new BooleanSetting("С удачей").setVisible(() -> filter.get() && filterSelect.getIndex() == 3);
    public SliderSetting udachalvl = new SliderSetting("Уровень удачи", 3, 1, 5, 1).setVisible(() -> filter_pickaxe_udacha.get() && filterSelect.getIndex() == 3);
    public BooleanSetting filter_pickaxe_prochnost = new BooleanSetting("С прочностью").setVisible(() -> filter.get() && filterSelect.getIndex() == 3);
    public SliderSetting procnhostlvl = new SliderSetting("Уровень прочности", 3, 1, 5, 1).setVisible(() -> filter_pickaxe_prochnost.get() && filterSelect.getIndex() == 3);
    public BooleanSetting filter_pickaxe_buldozer = new BooleanSetting("С бульдозером").setVisible(() -> filter.get() && filterSelect.getIndex() == 3);
    public SliderSetting buldozerlvl = new SliderSetting("Уровень бульдозера", 3, 1, 3, 1).setVisible(() -> filter_pickaxe_buldozer.get() && filterSelect.getIndex() == 3);
    public BooleanSetting filter_pickaxe_shelk = new BooleanSetting("С шёлковым касанием").setVisible(() -> filter.get() && filterSelect.getIndex() == 3);
    public BooleanSetting filter_pickaxe_mending = new BooleanSetting("С починкой").setVisible(() -> filter.get() && filterSelect.getIndex() == 3);


    @Override
    public void event(Event event) {
        if (event instanceof EventUpdate) {
            if (mc.currentScreen instanceof ChestScreen e) {
                if (e.getTitle().getString().contains("Аукцион") || e.getTitle().getString().contains("Поиск:")) {
                    Container container = e.getContainer();
                    Slot slot1 = null;
                    Slot slot2 = null;
                    Slot slot3 = null;
                    int fsPrice = Integer.MAX_VALUE;
                    int medPrice = Integer.MAX_VALUE;
                    int thPrice = Integer.MAX_VALUE;

                    for (Slot slot : container.inventorySlots) {
                        if (slot.slotNumber > 44 || slot.getStack().isEmpty()) {
                            continue;
                        }

                        if (!isItemValid(slot.getStack())) {
                            continue;
                        }

                        if (ClientHelper.isConnectedToServer("spookytime")) {
                            if (showNBT.get() && e.getTitle().getString().contains("Поиск:")) {
                                CompoundNBT nbt = slot.getStack().getTag();
                                if (nbt != null) {
                                    AbstractCommand.addMessage("§6NBT для предмета " + slot.getStack().getDisplayName().getString() + ":");
                                    AbstractCommand.addMessage("§f" + nbt.toString());
                                }
                            }
                        }

                        if (showPricePerItem.get()) {
                            addPricePerItemToLore(slot.getStack());
                        }

                        int count = slot.getStack().getCount();
                        int currentPrice = -1;
                        if (count > 0) {
                            currentPrice = extractPriceFromStack(slot.getStack()) / count;
                        }
                        if (currentPrice != -1 && currentPrice < fsPrice) {
                            fsPrice = currentPrice;
                            slot1 = slot;
                        }
                        if (currentPrice != -1 && currentPrice < medPrice && currentPrice > fsPrice) {
                            medPrice = currentPrice;
                            slot2 = slot;
                        }
                        if (currentPrice != -1 && currentPrice < thPrice && currentPrice > medPrice) {
                            thPrice = currentPrice;
                            slot3 = slot;
                        }
                    }

                    if (slot1 != null && !slot1.getStack().isEmpty()) {
                        x = (slot1.xPos);
                        y = (slot1.yPos);
                    } else {
                        x = (0);
                    }
                    if (slot2 != null && !slot2.getStack().isEmpty()) {
                        x2 = (slot2.xPos);
                        y2 = (slot2.yPos);
                    } else {
                        x2 = (0);
                    }
                    if (slot3 != null && !slot3.getStack().isEmpty()) {
                        x3 = (slot3.xPos);
                        y3 = (slot3.yPos);
                    } else {
                        x3 = (0);
                    }
                } else {
                    x = (0);
                    x2 = (0);
                    x3 = (0);
                }
            } else {
                x = (0);
                x2 = (0);
                x3 = (0);
            }
        }
    }
   /* private void printItemLore(ItemStack stack) {
        if (stack.isEmpty()) return;

        CMD.addMessage("§6=== Информация о предмете ===");
        CMD.addMessage("§fНазвание: §a" + stack.getDisplayName().getString());

        int price = extractPriceFromStack(stack);
        if (price != -1) {
            CMD.addMessage("§fЦена: §a" + formatNumber(price) + "$");
        } else {
            CMD.addMessage("§cЦена не найдена");
        }

        if (ClientHelper.isConnectedToServer("spookytime")) {
            List<ITextComponent> tooltip = stack.getTooltip(mc.player,
                    mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED
                            : ITooltipFlag.TooltipFlags.NORMAL);

            for (ITextComponent line : tooltip) {
                String text = line.getString();
                if (text.contains("Продавец:") || text.contains("Seller:")) {
                    CMD.addMessage("§f" + text.trim());
                }
                if (text.contains("Истекает:") || text.contains("Expires:")) {
                    CMD.addMessage("§f" + text.trim());
                }
            }
        }

        CMD.addMessage("§6============================");
    }*/

    public float x = 0;
    public float y = 0;
    public float x2 = 0;
    public float y2 = 0;
    public float x3 = 0;
    public float y3 = 0;

    private void addPricePerItemToLore(ItemStack stack) {
        if (stack.isEmpty() || stack.getCount() <= 1) return;

        int totalPrice = extractPriceFromStack(stack);
        if (totalPrice <= 0) return;

        int pricePerItem = totalPrice / stack.getCount();

        String formattedPrice = formatNumber(pricePerItem);
        if (formattedPrice == null || formattedPrice.isEmpty()) {
            formattedPrice = String.valueOf(pricePerItem);
        }

        try {
            CompoundNBT tag = stack.getTag();
            if (tag == null) {
                tag = new CompoundNBT();
                stack.setTag(tag);
            }

            CompoundNBT display;
            if (tag.contains("display", 10)) {
                display = tag.getCompound("display");
            } else {
                display = new CompoundNBT();
                tag.put("display", display);
            }

            ListNBT lore;
            if (display.contains("Lore", 9)) {
                lore = display.getList("Lore", 8);
            } else {
                lore = new ListNBT();
                display.put("Lore", lore);
            }

            for (int i = 0; i < lore.size(); i++) {
                String loreString = lore.getString(i);
                if (loreString.contains("Цена за 1 предмет")) {
                    lore.remove(i);
                    i--;
                }
            }

            int sellerIndex = -1;
            for (int i = 0; i < lore.size(); i++) {
                String loreString = lore.getString(i);
                if (loreString.toLowerCase().contains("продавец")) {
                    sellerIndex = i;
                    break;
                }
            }

            if (sellerIndex > 0) {
                String priceText = "{\"text\":\"§a$§f Цена за 1 предмет: §a$" + formattedPrice + "\"}";
                lore.add(sellerIndex, StringNBT.valueOf(priceText));
            } else {
                int priceLineIndex = -1;
                for (int i = 0; i < lore.size(); i++) {
                    try {
                        String loreString = lore.getString(i);
                        JsonObject object = new JsonParser().parse(loreString).getAsJsonObject();

                        if (object.has("extra")) {
                            JsonArray array = object.getAsJsonArray("extra");
                            if (array.size() > 2) {
                                for (int j = 0; j < array.size(); j++) {
                                    if (!array.get(j).isJsonObject()) continue;
                                    JsonObject element = array.get(j).getAsJsonObject();
                                    if (element.has("text") &&
                                        element.get("text").getAsString().trim().toLowerCase().contains("цен")) {
                                        priceLineIndex = i;
                                        break;
                                    }
                                }
                            }
                        }
                        if (priceLineIndex != -1) break;
                    } catch (Exception e) {
                    }
                }
                if (priceLineIndex != -1) {
                    String priceText = "{\"text\":\"§a$§f Цена за 1 предмет: §a$" + formattedPrice + "\"}";
                    lore.add(priceLineIndex + 1, StringNBT.valueOf(priceText));
                } else {
                    String priceText = "{\"text\":\"§a$§f Цена за 1 предмет: §a$" + formattedPrice + "\"}";
                    if (lore.size() > 0) {
                        lore.add(0, StringNBT.valueOf(priceText));
                    } else {
                        lore.add(StringNBT.valueOf(priceText));
                    }
                }
            }

            display.put("Lore", lore);
            tag.put("display", display);
            stack.setTag(tag);
        } catch (Exception e) {
            AbstractCommand.addMessage("Ошибка при добавлении цены за 1 предмет: " + e.getMessage());
        }
    }

    private String formatNumber(int number) {
        NumberFormat format = NumberFormat.getInstance(Locale.US);
        return format.format(number);
    }

    public boolean isItemValid(ItemStack stack) {
        if (!filter.get()) return true;

        Item item = stack.getItem();
        List<Item> checkingItems = List.of(
                Items.SPLASH_POTION,
                Items.NETHERITE_BOOTS,
                Items.DIAMOND_BOOTS,
                Items.NETHERITE_LEGGINGS,
                Items.DIAMOND_LEGGINGS,
                Items.NETHERITE_CHESTPLATE,
                Items.DIAMOND_CHESTPLATE,
                Items.NETHERITE_HELMET,
                Items.DIAMOND_HELMET,
                Items.NETHERITE_SWORD,
                Items.DIAMOND_SWORD,
                Items.DIAMOND_HOE,
                Items.NETHERITE_HOE,
                Items.DIAMOND_PICKAXE,
                Items.NETHERITE_PICKAXE
        );

        if (checkingItems.contains(item)) {
            if (item == Items.SPLASH_POTION) {
                List<EffectInstance> effects = PotionUtils.getFullEffectsFromItem(stack);
                List<List<EffectInstance>> validPotions = List.of(
                        List.of(new EffectInstance(Effects.STRENGTH, 180 * 20, 3)),
                        List.of(new EffectInstance(Effects.STRENGTH, 90 * 20, 3)),
                        List.of(new EffectInstance(Effects.HEALTH_BOOST, 45 * 20, 2),
                                new EffectInstance(Effects.REGENERATION, 45 * 20, 2)),
                        List.of(new EffectInstance(Effects.HEALTH_BOOST, 180 * 20, 1),
                                new EffectInstance(Effects.REGENERATION, 60 * 20, 1)),
                        List.of(new EffectInstance(Effects.SLOWNESS, 90 * 20, 3),
                                new EffectInstance(Effects.WEAKNESS, 90 * 20, 2)),
                        List.of(new EffectInstance(Effects.SLOWNESS, 180 * 20, 2)),
                        List.of(new EffectInstance(Effects.STRENGTH, 300 * 20, 2),
                                new EffectInstance(Effects.SPEED, 900 * 20, 2),
                                new EffectInstance(Effects.INVISIBILITY, 900 * 20, 0)),
                        List.of(new EffectInstance(Effects.STRENGTH, 300 * 20, 2),
                                new EffectInstance(Effects.SPEED, 900 * 20, 2),
                                new EffectInstance(Effects.INVISIBILITY, 900 * 20, 1)),
                        List.of(new EffectInstance(Effects.BLINDNESS, 20 * 20, 0),
                                new EffectInstance(Effects.GLOWING, 240 * 20, 0)),
                        List.of(new EffectInstance(Effects.SPEED, 180 * 20, 2),
                                new EffectInstance(Effects.JUMP_BOOST, 180 * 20, 0)),
                        List.of(  new EffectInstance(Effects.INVISIBILITY, 900 * 20, 0),
                                new EffectInstance(Effects.FIRE_RESISTANCE, 900 * 20, 0),
                                new EffectInstance(Effects.SPEED, 900 * 20, 2),
                                new EffectInstance(Effects.HASTE, 180 * 20, 0),
                                new EffectInstance(Effects.STRENGTH, 300 * 20, 2))

                );


                return validPotions.stream()
                        .filter(potionEffects -> potionEffects.size() <= effects.size())
                        .anyMatch(potionEffects -> {
                            for (EffectInstance requiredEffect : potionEffects) {
                                boolean found = false;
                                for (EffectInstance itemEffect : effects) {
                                    if (itemEffect.getPotion() == requiredEffect.getPotion() &&
                                        itemEffect.getAmplifier() == requiredEffect.getAmplifier() &&
                                        itemEffect.getDuration() == requiredEffect.getDuration()) {
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    return false;
                                }
                            }
                            return true;
                        });
            } else if ((item == Items.NETHERITE_BOOTS || item == Items.NETHERITE_LEGGINGS || item == Items.NETHERITE_CHESTPLATE || item == Items.NETHERITE_HELMET || item == Items.DIAMOND_BOOTS || item == Items.DIAMOND_LEGGINGS || item == Items.NETHERITE_CHESTPLATE || item == Items.DIAMOND_HELMET)) {
                Map<Enchantment, Integer> ench = EnchantmentHelper.getEnchantments(stack);

                boolean hasProtection5 = ench.containsKey(Enchantments.PROTECTION) && ench.get(Enchantments.PROTECTION) >= 5;
                boolean hasMending = ench.containsKey(Enchantments.MENDING);
                boolean hasUnbreaking5 = ench.containsKey(Enchantments.UNBREAKING) && ench.get(Enchantments.UNBREAKING) >= 5;
                boolean hasPodvodka3 = ench.containsKey(Enchantments.DEPTH_STRIDER) && ench.get(Enchantments.DEPTH_STRIDER) >= 3;
                boolean hasThorns = ench.containsKey(Enchantments.THORNS);

                if (filter_armor_protection5.get() && !hasProtection5) return false;
                if (filter_armor_pochinka.get() && !hasMending) return false;
                if (!filter_armor_pochinka.get() && hasMending) return false;
                if (filter_armor_prochnost5.get() && !hasUnbreaking5) return false;
                if (item == Items.NETHERITE_BOOTS) {
                    if (filter_armor_podvodka3.get() && !hasPodvodka3) return false;
                }

                return filter_armor_ship.get() == hasThorns;
            } else if (item == Items.NETHERITE_SWORD || item == Items.DIAMOND_SWORD) {
                Map<Enchantment, Integer> ench = EnchantmentHelper.getEnchantments(stack);

                boolean hasSharpness7 = ench.containsKey(Enchantments.SHARPNESS) && ench.get(Enchantments.SHARPNESS) >= 7;
                boolean hasKnockback = ench.containsKey(Enchantments.KNOCKBACK);
                boolean haslooting = ench.containsKey(Enchantments.LOOTING);
                int knockbackLevel = hasKnockback ? ench.get(Enchantments.KNOCKBACK) : 0;

                int opitniyLevel = Math.round(opitniylvl.get());
                Enchant opitniy = new Enchant("Опытный", opitniyLevel);
                boolean hasopitniy = opitniy.has(stack);

                int vampirLevel = Math.round(vampirlvl.get());
                Enchant vampir = new Enchant("Вампиризм", vampirLevel);
                boolean hasvampir = vampir.has(stack);

                int okislLevel = Math.round(okisllvl.get());
                Enchant okisl = new Enchant("Окисление", okislLevel);
                boolean hasokisl = okisl.has(stack);

                int yadLevel = Math.round(yadlvl.get());
                Enchant yad = new Enchant("Яд", yadLevel);
                boolean hasyad = yad.has(stack);


                if (filter_sword_sharpness7.get() && !hasSharpness7) return false;

                if (filter_sword_knockback.get()) {
                    if (!hasKnockback || knockbackLevel < 1) return false;
                } else {
                    if (hasKnockback) return false;
                    if (filter_sword_looting.get() && !haslooting) return false;
                    if (filter_sword_opitniy.get() && !hasopitniy) return false;
                    if (filter_sword_vampir.get() && !hasvampir) return false;
                    if (filter_sword_okisl.get() && !hasokisl) return false;
                    if (filter_sword_yad.get() && !hasyad) return false;

                }
            } else if (item == Items.NETHERITE_HOE || item == Items.DIAMOND_HOE) {
                Map<Enchantment, Integer> ench = EnchantmentHelper.getEnchantments(stack);

                boolean hasUnbreaking5 = ench.containsKey(Enchantments.UNBREAKING) && ench.get(Enchantments.UNBREAKING) >= 5;
                boolean hasFortune5 = ench.containsKey(Enchantments.FORTUNE) && ench.get(Enchantments.FORTUNE) >= 5;
                boolean hasMending = ench.containsKey(Enchantments.MENDING);


                Enchant greenThumb = new Enchant("Озеленитель", 3);
                boolean hasGreenThumb = greenThumb.has(stack);

                if (filter_hoe_prochnost5.get() && !hasUnbreaking5) return false;
                if (filter_hoe_udacha5.get() && !hasFortune5) return false;
                if (filter_hoe_mending.get() && !hasMending) return false;
                if (!filter_hoe_mending.get() && hasMending) return false;
                if (filter_hoe_ozelenitel.get() && !hasGreenThumb) return false;
            } else if (item == Items.NETHERITE_PICKAXE || item == Items.DIAMOND_PICKAXE) {
                Map<Enchantment, Integer> ench = EnchantmentHelper.getEnchantments(stack);

                boolean hasEff = ench.containsKey(Enchantments.EFFICIENCY) && ench.get(Enchantments.EFFICIENCY) >= efflvl.get();
                boolean hasFortune = ench.containsKey(Enchantments.FORTUNE) && ench.get(Enchantments.FORTUNE) >= udachalvl.get();
                boolean hasUnbreaking = ench.containsKey(Enchantments.UNBREAKING) && ench.get(Enchantments.UNBREAKING) >= procnhostlvl.get();
                boolean hasShelk = ench.containsKey(Enchantments.SILK_TOUCH) && ench.get(Enchantments.SILK_TOUCH) >= 1;
                boolean hasMending = ench.containsKey(Enchantments.MENDING);


                int buldozerLevel = Math.round(buldozerlvl.get());
                Enchant buldozer = new Enchant("Бульдозер", buldozerLevel);
                boolean hasbuldozer = buldozer.has(stack);

                if (filter_pickaxe_eff.get() && !hasEff) return false;
                if (filter_pickaxe_udacha.get() && !hasFortune) return false;
                if (filter_pickaxe_prochnost.get() && !hasUnbreaking) return false;
                if (filter_pickaxe_buldozer.get() && !hasbuldozer) return false;
                if (filter_pickaxe_shelk.get() && !hasShelk) return false;
                if (filter_pickaxe_mending.get() && !hasMending) return false;
                if (!filter_pickaxe_mending.get() && hasMending) return false;

            }
        }
        return true;
    }

    protected int extractPriceFromStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return -1;
        }

        if (ClientHelper.isConnectedToServer("spookytime") || ClientHelper.isConnectedToServer("holyworld")) {
            if (!(mc.currentScreen instanceof ChestScreen)) {
                return -1;
            }

            ChestScreen screen = (ChestScreen) mc.currentScreen;
            String title = screen.getTitle().getString();

            if (!title.contains("Аукцион") && !title.contains("Поиск:")) {
                return -1;
            }

            List<ITextComponent> tooltip = stack.getTooltip(mc.player,
                    mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED
                            : ITooltipFlag.TooltipFlags.NORMAL);

            for (ITextComponent line : tooltip) {
                String text = line.getString();
                if (text.contains("Цена:") || text.contains("Price:")) {
                    try {
                        int priceIndex = Math.max(text.indexOf("Цена:"), text.indexOf("Price:"));
                        if (priceIndex != -1) {
                            String priceText = text.substring(priceIndex);

                            String priceStr = priceText.replaceAll("[^0-9.]", "").trim();

                            try {
                                return (int) (Double.parseDouble(priceStr));
                            } catch (NumberFormatException e) {
                                try {
                                    return Integer.parseInt(priceStr);
                                } catch (NumberFormatException e2) {
                                    return -1;
                                }

                            }
                        } else {
                            return -1;
                        }

                    } catch (Exception e) {
                        return -1;
                    }
                }
            }
        } else {
            CompoundNBT tag = stack.getTag();
            if (tag != null && tag.contains("display", 10)) {
                CompoundNBT display = tag.getCompound("display");
                if (display.contains("Lore", 9)) {
                    ListNBT lore = display.getList("Lore", 8);
                    for (int j = 0; j < lore.size(); ++j) {
                        try {
                            JsonObject object = new JsonParser().parse(lore.getString(j)).getAsJsonObject();
                            if (object.has("extra")) {
                                JsonArray array = object.getAsJsonArray("extra");
                                if (array.size() > 2) {
                                    JsonObject title = array.get(1).getAsJsonObject();
                                    if (title.get("text").getAsString().trim().toLowerCase().contains("ценa")) {
                                        String line = array.get(2).getAsJsonObject().get("text").getAsString()
                                                .trim().substring(1).replaceAll("[ ,]", "");
                                        return Integer.parseInt(line);
                                    }
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }

        return -1;
    }
}
