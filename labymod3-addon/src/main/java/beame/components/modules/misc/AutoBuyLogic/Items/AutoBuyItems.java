

package beame.components.modules.misc.AutoBuyLogic.Items;

import beame.Essence;
import beame.components.modules.misc.AutoBuyLogic.AutoBuyUtil;
import beame.feature.notify.NotificationManager.Type;
import beame.util.ClientHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;

public class AutoBuyItems {
// leaked by itskekoff; discord.gg/sk3d bPddtvVx
    public List<AutoBuyItemClass> list = new ArrayList();
    private final Map<Item, List<AutoBuyItemClass>> itemMap = new HashMap<>();

    public AutoBuyItems() {
        this.addItems();
        this.rebuildMap();
    }

    private void addItems() {
        if (!this.list.isEmpty()) {
            this.list.clear();
        }

        List<Enchant> repairBookEnchants = new ArrayList();
        repairBookEnchants.add(new Enchant(Enchantments.MENDING, 1));
        HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> karatel = new HashMap();
        if (!ClientHelper.isConnectedToServer("spookytime")) {
            karatel.putAll(Map.of(Attributes.MOVEMENT_SPEED, Map.entry(0.1F, Operation.MULTIPLY_BASE), Attributes.MAX_HEALTH, Map.entry(-4.0F, Operation.ADDITION), Attributes.ATTACK_DAMAGE, Map.entry(7.0F, Operation.ADDITION)));
        }

        HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> krush = new HashMap();
        if (!ClientHelper.isConnectedToServer("spookytime")) {
            krush.putAll(Map.of(Attributes.ARMOR, Map.entry(2.0F, Operation.ADDITION), Attributes.ARMOR_TOUGHNESS, Map.entry(2.0F, Operation.ADDITION), Attributes.ATTACK_DAMAGE, Map.entry(3.0F, Operation.ADDITION), Attributes.MAX_HEALTH, Map.entry(4.0F, Operation.ADDITION)));
        }

        HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> dedala3 = new HashMap();
        if (!ClientHelper.isConnectedToServer("spookytime")) {
            dedala3.putAll(Map.of(Attributes.ATTACK_DAMAGE, Map.entry(5.0F, Operation.ADDITION), Attributes.MAX_HEALTH, Map.entry(-4.0F, Operation.ADDITION)));
        }

        HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> exidna3 = new HashMap();
        if (!ClientHelper.isConnectedToServer("spookytime")) {
            exidna3.putAll(Map.of(Attributes.ATTACK_DAMAGE, Map.entry(6.0F, Operation.ADDITION), Attributes.MAX_HEALTH, Map.entry(-4.0F, Operation.ADDITION), Attributes.ARMOR, Map.entry(-2.0F, Operation.ADDITION), Attributes.ARMOR_TOUGHNESS, Map.entry(-2.0F, Operation.ADDITION)));
        }

        HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> grani3 = new HashMap();
        if (!ClientHelper.isConnectedToServer("spookytime")) {
            grani3.putAll(Map.of(Attributes.ATTACK_DAMAGE, Map.entry(3.0F, Operation.ADDITION), Attributes.MAX_HEALTH, Map.entry(-4.0F, Operation.ADDITION), Attributes.MOVEMENT_SPEED, Map.entry(0.15F, Operation.MULTIPLY_BASE)));
        }

        HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> harmonia3 = new HashMap();
        if (!ClientHelper.isConnectedToServer("spookytime")) {
            harmonia3.putAll(Map.of(Attributes.ATTACK_DAMAGE, Map.entry(2.0F, Operation.ADDITION), Attributes.MAX_HEALTH, Map.entry(2.0F, Operation.ADDITION), Attributes.ARMOR, Map.entry(2.0F, Operation.ADDITION)));
        }

        HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> triton3 = new HashMap();
        if (!ClientHelper.isConnectedToServer("spookytime")) {
            triton3.putAll(Map.of(Attributes.ARMOR, Map.entry(3.0F, Operation.ADDITION), Attributes.MAX_HEALTH, Map.entry(2.0F, Operation.ADDITION), Attributes.ARMOR_TOUGHNESS, Map.entry(-3.0F, Operation.ADDITION)));
        }

        HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> phoenix3 = new HashMap();
        if (!ClientHelper.isConnectedToServer("spookytime")) {
            phoenix3.putAll(Map.of(Attributes.MAX_HEALTH, Map.entry(6.0F, Operation.ADDITION), Attributes.ATTACK_SPEED, Map.entry(0.1F, Operation.MULTIPLY_BASE)));
        }

        HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> andromeda3 = new HashMap();
        if (!ClientHelper.isConnectedToServer("spookytime")) {
            andromeda3.putAll(Map.of(Attributes.MOVEMENT_SPEED, Map.entry(0.15F, Operation.MULTIPLY_BASE), Attributes.MAX_HEALTH, Map.entry(-4.0F, Operation.ADDITION), Attributes.ATTACK_DAMAGE, Map.entry(3.0F, Operation.ADDITION), Attributes.ARMOR, Map.entry(2.0F, Operation.ADDITION)));
        }

        HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> pandora3 = new HashMap();
        if (!ClientHelper.isConnectedToServer("spookytime")) {
            pandora3.putAll(Map.of(Attributes.MOVEMENT_SPEED, Map.entry(0.15F, Operation.MULTIPLY_BASE), Attributes.ARMOR, Map.entry(-0.15F, Operation.MULTIPLY_BASE), Attributes.ATTACK_DAMAGE, Map.entry(0.25F, Operation.MULTIPLY_BASE)));
        }

        HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> titan3 = new HashMap();
        if (!ClientHelper.isConnectedToServer("spookytime")) {
            titan3.putAll(Map.of(Attributes.MOVEMENT_SPEED, Map.entry(-0.15F, Operation.MULTIPLY_BASE), Attributes.ARMOR_TOUGHNESS, Map.entry(2.0F, Operation.ADDITION), Attributes.ARMOR, Map.entry(2.0F, Operation.ADDITION)));
        }

        HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> apollon3 = new HashMap();
        if (!ClientHelper.isConnectedToServer("spookytime")) {
            apollon3.putAll(Map.of(Attributes.ATTACK_DAMAGE, Map.entry(4.0F, Operation.ADDITION), Attributes.MOVEMENT_SPEED, Map.entry(-0.1F, Operation.MULTIPLY_BASE)));
        }

        HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> astrey3 = new HashMap();
        if (!ClientHelper.isConnectedToServer("spookytime")) {
            astrey3.putAll(Map.of(Attributes.ATTACK_DAMAGE, Map.entry(3.0F, Operation.ADDITION), Attributes.MAX_HEALTH, Map.entry(4.0F, Operation.ADDITION), Attributes.ATTACK_SPEED, Map.entry(-0.15F, Operation.MULTIPLY_BASE)));
        }

        HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> osiris3 = new HashMap();
        if (!ClientHelper.isConnectedToServer("spookytime")) {
            osiris3.putAll(Map.of(Attributes.ARMOR, Map.entry(3.0F, Operation.ADDITION), Attributes.ATTACK_KNOCKBACK, Map.entry(0.15F, Operation.MULTIPLY_BASE), Attributes.KNOCKBACK_RESISTANCE, Map.entry(-0.15F, Operation.MULTIPLY_BASE)));
        }

        HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> ximera3 = new HashMap();
        if (!ClientHelper.isConnectedToServer("spookytime")) {
            ximera3.putAll(Map.of(Attributes.ATTACK_DAMAGE, Map.entry(3.0F, Operation.ADDITION), Attributes.ATTACK_SPEED, Map.entry(0.15F, Operation.MULTIPLY_BASE), Attributes.MAX_HEALTH, Map.entry(-2.0F, Operation.ADDITION)));
        }

        List<Enchant> krushhelmet = new ArrayList(Arrays.asList(new Enchant(Enchantments.AQUA_AFFINITY, -1), new Enchant(Enchantments.BLAST_PROTECTION, 5), new Enchant(Enchantments.FIRE_PROTECTION, 5), new Enchant(Enchantments.MENDING, -1), new Enchant(Enchantments.PROJECTILE_PROTECTION, 5), new Enchant(Enchantments.PROTECTION, 5), new Enchant(Enchantments.RESPIRATION, 3), new Enchant(Enchantments.UNBREAKING, 5)));
        List<Enchant> krushChestplateEnchants = new ArrayList(Arrays.asList(new Enchant(Enchantments.BLAST_PROTECTION, 5), new Enchant(Enchantments.FIRE_PROTECTION, 5), new Enchant(Enchantments.MENDING, -1), new Enchant(Enchantments.PROJECTILE_PROTECTION, 5), new Enchant(Enchantments.PROTECTION, 5), new Enchant(Enchantments.UNBREAKING, 5)));
        List<Enchant> krushLegginsEnchants = new ArrayList(Arrays.asList(new Enchant(Enchantments.BLAST_PROTECTION, 5), new Enchant(Enchantments.FIRE_PROTECTION, 5), new Enchant(Enchantments.MENDING, -1), new Enchant(Enchantments.PROJECTILE_PROTECTION, 5), new Enchant(Enchantments.PROTECTION, 5), new Enchant(Enchantments.UNBREAKING, 5)));
        List<Enchant> krushBootsEnchants = new ArrayList(Arrays.asList(new Enchant(Enchantments.BLAST_PROTECTION, 5), new Enchant(Enchantments.DEPTH_STRIDER, 3), new Enchant(Enchantments.FEATHER_FALLING, 4), new Enchant(Enchantments.FIRE_PROTECTION, 5), new Enchant(Enchantments.MENDING, -1), new Enchant(Enchantments.PROJECTILE_PROTECTION, 5), new Enchant(Enchantments.PROTECTION, 5), new Enchant(Enchantments.SOUL_SPEED, 3), new Enchant(Enchantments.UNBREAKING, 5)));
        List<Enchant> krushSwordEnchants = new ArrayList(Arrays.asList(new Enchant(Enchantments.BANE_OF_ARTHROPODS, 7), new Enchant(Enchantments.FIRE_ASPECT, 2), new Enchant(Enchantments.LOOTING, 5), new Enchant(Enchantments.MENDING, -1), new Enchant(Enchantments.SHARPNESS, 7), new Enchant(Enchantments.SMITE, 7), new Enchant(Enchantments.SWEEPING, 3), new Enchant(Enchantments.UNBREAKING, 5), new Enchant("Яд", 3), new Enchant("Опытный", 3), new Enchant("Вампиризм", 2), new Enchant("Окисление", 2), new Enchant("Детекция", 3)));
        List<Enchant> krushTrebEnchants = new ArrayList(Arrays.asList(new Enchant(Enchantments.CHANNELING, -1), new Enchant(Enchantments.FIRE_ASPECT, 2), new Enchant(Enchantments.IMPALING, 5), new Enchant(Enchantments.LOYALTY, 3), new Enchant(Enchantments.MENDING, -1), new Enchant(Enchantments.SHARPNESS, 7), new Enchant(Enchantments.UNBREAKING, 5), new Enchant("Яд", 3), new Enchant("Опытный", 3), new Enchant("Вампиризм", 2), new Enchant("Окисление", 2), new Enchant("Детекция", 3), new Enchant("Ступор", 3), new Enchant("Скаут", 3), new Enchant("Притяжение", 2)));
        List<Enchant> krushArbEnchants = new ArrayList(Arrays.asList(new Enchant(Enchantments.MENDING, -1), new Enchant(Enchantments.MULTISHOT, -1), new Enchant(Enchantments.PIERCING, 5), new Enchant(Enchantments.QUICK_CHARGE, 3), new Enchant(Enchantments.UNBREAKING, 3)));
        List<Enchant> krushPickaxeEnchants = new ArrayList(Arrays.asList(new Enchant(Enchantments.EFFICIENCY, 10), new Enchant(Enchantments.FORTUNE, 5), new Enchant(Enchantments.MENDING, -1), new Enchant(Enchantments.UNBREAKING, 5), new Enchant("Бульдозер", 2), new Enchant("Авто-плавка", -1), new Enchant("Опытный", -3), new Enchant("Пингер", -1), new Enchant("Магнит", -1), new Enchant("Паутина", -1)));
        List<Enchant> krushElytraEnchants = new ArrayList(Arrays.asList(new Enchant(Enchantments.MENDING, -1), new Enchant(Enchantments.UNBREAKING, 5)));
        new ArrayList(Arrays.asList(new Enchant(Enchantments.MENDING, -1)));
        this.list.addAll(List.of(
                new AutoBuyItemClass("Шлем Крушителя", 0, Items.NETHERITE_HELMET, krushhelmet, null, ""),
                new AutoBuyItemClass("Нагрудник Крушителя", 0, Items.NETHERITE_CHESTPLATE, krushChestplateEnchants, null, ""),
                new AutoBuyItemClass("Поножи Крушителя", 0, Items.NETHERITE_LEGGINGS, krushLegginsEnchants, null, ""),
                new AutoBuyItemClass("Ботинки Крушителя", 0, Items.NETHERITE_BOOTS, krushBootsEnchants, null, ""),
                new AutoBuyItemClass("Меч Крушителя", 0, Items.NETHERITE_SWORD, krushSwordEnchants, null, ""),
                new AutoBuyItemClass("Трезубец Крушителя", 0, Items.TRIDENT, krushTrebEnchants, null, ""),
                new AutoBuyItemClass("Арбалет Крушителя", 0, Items.CROSSBOW, krushArbEnchants, null, ""),
                new AutoBuyItemClass("Кирка Крушителя", 0, Items.NETHERITE_PICKAXE, krushPickaxeEnchants, null, ""),
                new AutoBuyItemClass("Талисман Карателя", 0, Items.TOTEM_OF_UNDYING, karatel, "attribute-item-tkaratela", "1"),
                new AutoBuyItemClass("Талисман Крушителя", 0, Items.TOTEM_OF_UNDYING, krush, "attribute-item-tkryshitela", "1"),
                new AutoBuyItemClass("Талисман Дедала", 0, Items.TOTEM_OF_UNDYING, dedala3, "attribute-item-tdedala", "1"),
                new AutoBuyItemClass("Талисман Ехидны", 0, Items.TOTEM_OF_UNDYING, exidna3, "attribute-item-texidni", "1"),
                new AutoBuyItemClass("Талисман Грани", 0, Items.TOTEM_OF_UNDYING, grani3, "attribute-item-tgrani", "1"),
                new AutoBuyItemClass("Талисман Гармонии", 0, Items.TOTEM_OF_UNDYING, harmonia3, "attribute-item-tgarmonii", "1"),
                new AutoBuyItemClass("Талисман Тритона", 0, Items.TOTEM_OF_UNDYING, triton3, "attribute-item-ttritona", "1"),
                new AutoBuyItemClass("Талисман Феникса", 0, Items.TOTEM_OF_UNDYING, phoenix3, "attribute-item-tfeniksa", "1"),
                new AutoBuyItemClass("Тотем бессмертия", 0, Items.TOTEM_OF_UNDYING),
                new AutoBuyItemClass("Сфера Андромеды", 0, Items.PLAYER_HEAD, andromeda3, "attribute-item-sandromedi", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDRmZmUzZjM1OGYyMDliYWQ4ZmZmNGRjNDgyNDVkOWJhZjBhMDMxYjNjMWVlNmI3NTg0NjBhMzM5YjE1MTllMiJ9fX0="),
                new AutoBuyItemClass("Сфера Пандора", 0, Items.PLAYER_HEAD, pandora3, "attribute-item-spandori", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGU1MWU2NWViNDA1Mjc3MjM4MmM5ZTUwN2E1NGJkZWQ0M2UzOWY3NTViNWRkZjU1YjNmMzk0NDNjZWQ0NjdmNCJ9fX0="),
                new AutoBuyItemClass("Сфера Титана", 0, Items.PLAYER_HEAD, titan3, "attribute-item-stitana", "ewogICJ0aW1lc3RhbXAiIDogMTcxNzM2NjY0NDcyNiwKICAicHJvZmlsZUlkIiA6ICJmMGZiOGE4NjIwNDY0MWZiOGZhYzJmNWZhMDQ0ZjNjMiIsCiAgInByb2ZpbGVOYW1lIiA6ICJBbnRob255MTAwNDA5IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2EwMzdiYmJlYjYyZTEwMjBkZjlhMDZjNGVkZDYwMzMwZTcwNjMwZDA5MGYwOTRkODc3NmMyYmQxMzVkZWMyMiIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9"),
                new AutoBuyItemClass("Сфера Аполлона", 0, Items.PLAYER_HEAD, apollon3, "attribute-item-sapollona", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjQxMTdiNjAxOGZlZjBkNTE1NjcyMTczZTNiMjZlNjYwZDY1MWU1ODc2YmE2ZDAzZTUzNDIyNzBjNDliZWM4MCJ9fX0"),
                new AutoBuyItemClass("Сфера Астрея", 0, Items.PLAYER_HEAD, astrey3, "attribute-item-sastreya", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWE1YWFkZDUyYTVmYWI5NzA4ODE0NTFhZGY1NmZiYjQ5M2EzNTg1NmVhOTZmNTRlMzJlZWE2NjJkNzg3ZWQyMCJ9fX0"),
                new AutoBuyItemClass("Сфера Осириса", 0, Items.PLAYER_HEAD, osiris3, "attribute-item-sosirisa", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDgxMzYzNWJkODZiMTcxYmJlMTQzYWQ3MWUwOTAyMjkyNjQ5Y2IzYWI4NDQwZWQwMGY4NWNhNmNhMzgyOTkzNiJ9fX0="),
                new AutoBuyItemClass("Сфера Химеры", 0, Items.PLAYER_HEAD, ximera3, "attribute-item-shimeri", "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWZhYmVlZDQyNGIyNTJhODk0NWE2NDQyYjQ2MmQ1ZjMxNDcwMWE4MTZkYTJkMGE2OWNjZGZjZmQ3NDZlNTg4ZSJ9fX0="),
                new AutoBuyItemClass("Пузырёк опыта", 0, Items.EXPERIENCE_BOTTLE),
                new AutoBuyItemClass("Чарка", 0, Items.ENCHANTED_GOLDEN_APPLE),
                new AutoBuyItemClass("Золотое яблоко", 0, Items.GOLDEN_APPLE),
                new AutoBuyItemClass("Яблоко", 0, Items.APPLE),
                new AutoBuyItemClass("Перка", 0, Items.ENDER_PEARL),
                new AutoBuyItemClass("Незеритовый слиток", 0, Items.NETHERITE_INGOT),
                new AutoBuyItemClass("Незеритовый лом", 0, Items.NETHERITE_SCRAP),
                new AutoBuyItemClass("Алмаз", 0, Items.DIAMOND),
                new AutoBuyItemClass("Изумруд", 0, Items.EMERALD),
                new AutoBuyItemClass("Золотой слиток", 0, Items.GOLD_INGOT),
                new AutoBuyItemClass("Алмазный блок", 0, Items.DIAMOND_BLOCK),
                new AutoBuyItemClass("Изумрудный блок", 0, Items.EMERALD_BLOCK),
                new AutoBuyItemClass("Золотой блок", 0, Items.GOLD_BLOCK),
                new AutoBuyItemClass("Обсидиан", 0, Items.OBSIDIAN),
                new AutoBuyItemClass("Голова дракона", 0, Items.DRAGON_HEAD),
                new AutoBuyItemClass("Голова визер-скелета", 0, Items.WITHER_SKELETON_SKULL),
                new AutoBuyItemClass("Древние обломки", 0, Items.ANCIENT_DEBRIS),
                new AutoBuyItemClass("Яйцо призыва крестьянина", 0, Items.VILLAGER_SPAWN_EGG),
                new AutoBuyItemClass("Яйцо зомби-крестьянина", 0, Items.ZOMBIE_VILLAGER_SPAWN_EGG),
                new AutoBuyItemClass("Элитры Крушителя", 0, Items.ELYTRA, krushElytraEnchants, null, ""),
                new AutoBuyItemClass("Элитры", 0, Items.ELYTRA),
                new AutoBuyItemClass("Золотая морковь", 0, Items.GOLDEN_CARROT),
                new AutoBuyItemClass("Шалкер", 0, Items.SHULKER_BOX),
                new AutoBuyItemClass("Маяк", 0, Items.BEACON),
                new AutoBuyItemClass("Алмазная руда", 0, Items.DIAMOND_ORE),
                new AutoBuyItemClass("Изумрудная руда", 0, Items.EMERALD_ORE),
                new AutoBuyItemClass("Спавнер", 0, Items.SPAWNER),
                new AutoBuyItemClass("Порох", 0, Items.GUNPOWDER),
                new AutoBuyItemClass("Проклятая душа", 0, Items.SOUL_LANTERN, "soul-currency", ""),
                new AutoBuyItemClass("Трапка", 0, Items.NETHERITE_SCRAP, "schematic-item-trap", ""),
                new AutoBuyItemClass("Дезориентация", 0, Items.ENDER_EYE, "effect-item-diz", ""),
                new AutoBuyItemClass("Явная пыль", 0, Items.SUGAR, "effect-item-dust", ""),
                new AutoBuyItemClass("Пласт", 0, Items.DRIED_KELP, "schematic-item-plast", ""),
                new AutoBuyItemClass("Божья аура", 0, Items.PHANTOM_MEMBRANE, "effect-item-god", ""),

                new AutoBuyItemClass("Зелье Отрыжки", 0, Items.SPLASH_POTION, Arrays.asList(new PotionEffectMatcher(2, 2, 180),
                        new PotionEffectMatcher(15, 0, 10), new PotionEffectMatcher(20, 4, 30),
                        new PotionEffectMatcher(17, 10, 90), new PotionEffectMatcher(24, 0, 180))),

                new AutoBuyItemClass("Зелье Медика", 0, Items.SPLASH_POTION, Arrays.asList(new PotionEffectMatcher(21, 2, 45),
                        new PotionEffectMatcher(10, 2, 45))),

                new AutoBuyItemClass("Зелье Киллера", 0, Items.SPLASH_POTION, Arrays.asList(new PotionEffectMatcher(11, 0, 180),
                        new PotionEffectMatcher(5, 3, 90))),

                new AutoBuyItemClass("Зелье Агента", 0, Items.SPLASH_POTION, Arrays.asList(new PotionEffectMatcher(12, 0, 900),
                        new PotionEffectMatcher(3, 0, 180), new PotionEffectMatcher(14, 0, 900),
                        new PotionEffectMatcher(1, 2, 900), new PotionEffectMatcher(5, 2, 300))),

                new AutoBuyItemClass("Зелье Победителя", 0, Items.SPLASH_POTION, Arrays.asList(new PotionEffectMatcher(11, 0, 60),
                        new PotionEffectMatcher(10, 1, 60), new PotionEffectMatcher(14, 0, 900), new PotionEffectMatcher(21, 1, 180))),

                new AutoBuyItemClass("Серная кислота", 0, Items.SPLASH_POTION, Arrays.asList(new PotionEffectMatcher(19, 1, 50),
                        new PotionEffectMatcher(2, 3, 90), new PotionEffectMatcher(18, 2, 90), new PotionEffectMatcher(20, 4, 30))),

                new AutoBuyItemClass("Вспышка", 0, Items.SPLASH_POTION, Arrays.asList(new PotionEffectMatcher(15, 0, 20),
                        new PotionEffectMatcher(24, 0, 240))),

                new AutoBuyItemClass("Моча флеша", 0, Items.SPLASH_POTION, Arrays.asList(new PotionEffectMatcher(8, 0, 180),
                        new PotionEffectMatcher(1, 2, 180))),

                new AutoBuyItemClass("Молот Тора", 0, Items.NETHERITE_PICKAXE, "radius-item-mega-buldozer", ""),
                new AutoBuyItemClass("Божье касание", 0, Items.GOLDEN_PICKAXE, "spawner-item-spawner-break", ""),
                new AutoBuyItemClass("Мощный удар", 0, Items.GOLDEN_PICKAXE, "bedrock-item-bedrock-break", ""),
                new AutoBuyItemClass("Книга починка", 0, Items.ENCHANTED_BOOK, new HashMap(), (String)null, "", repairBookEnchants, new ArrayList()),
                new AutoBuyItemClass("Отмычка к сферам", 0, Items.TRIPWIRE_HOOK, "spheres", ""),
                new AutoBuyItemClass("Отмычка к броне", 0, Items.TRIPWIRE_HOOK, "armors", ""),
                new AutoBuyItemClass("Отмычка к оружию", 0, Items.TRIPWIRE_HOOK, "weapons", ""),
                new AutoBuyItemClass("Отмычка к инструментам", 0, Items.TRIPWIRE_HOOK, "tools", ""),
                new AutoBuyItemClass("Отмычка к ресурсам", 0, Items.TRIPWIRE_HOOK, "resources", ""),
                new AutoBuyItemClass("Обычный мист", 0, Items.CAMPFIRE, "MILD", ""),
                new AutoBuyItemClass("Богатый мист", 0, Items.CAMPFIRE, "WEAK", ""),
                new AutoBuyItemClass("Легендарный мист", 0, Items.CAMPFIRE, "1", ""),
                new AutoBuyItemClass("Прогрузчик чанков 1x1", 0, Items.STRUCTURE_BLOCK, "executable-block-chunker-1", ""),
                new AutoBuyItemClass("Прогрузчик чанков 2x2", 0, Items.STRUCTURE_BLOCK, "executable-block-chunker-2", ""),
                new AutoBuyItemClass("Прогрузчик чанков 3x3", 0, Items.STRUCTURE_BLOCK, "executable-block-chunker-3", ""),
                new AutoBuyItemClass("Дамагер", 0, Items.JIGSAW, "executable-block-damager", ""),
                new AutoBuyItemClass("Динамит", 0, Items.TNT), new AutoBuyItemClass("Таер вайт", 0, Items.TNT, "tnt-item-white", ""),
                new AutoBuyItemClass("Таер блэк", 0, Items.TNT, "tnt-item-black", "")));
    }

    private void rebuildMap() {
        itemMap.clear();
        for (AutoBuyItemClass item : list) {
            itemMap.computeIfAbsent(item.item, k -> new ArrayList<>()).add(item);
        }
    }

    private List<AutoBuyItemClass> getCandidates(ItemStack stack) {
        return itemMap.getOrDefault(stack.getItem(), Collections.emptyList());
    }

    public void setPrice(AutoBuyItemClass item, String price) {
        if (price != null) {
            int targetIndex = -1;

            for(int i = 0; i < this.list.size(); ++i) {
                AutoBuyItemClass listItem = (AutoBuyItemClass)this.list.get(i);
                if (listItem.displayName.equals(item.displayName)) {
                    targetIndex = i;
                    break;
                }
            }

            if (targetIndex == -1) {
                Essence.getHandler().notificationManager.pushNotify("Неверный айди предмета", Type.Info);
            } else {
                AutoBuyItemClass targetItem = (AutoBuyItemClass)this.list.get(targetIndex);

                try {
                    boolean reset = price.isEmpty();
                    int price2 = Integer.parseInt(price);
                    if (!reset && price2 <= 0) {
                        Essence.getHandler().notificationManager.pushNotify("Цена должна быть больше 0.", Type.Info);
                        return;
                    }

                    targetItem.buyPrice = reset ? 0 : price2;
                    this.list.set(targetIndex, targetItem);
                } catch (NumberFormatException var7) {
                    Essence.getHandler().notificationManager.pushNotify("Неверная стоимость для покупки предмета!", Type.Info);
                    return;
                }

                Essence.getHandler().autoBuy.savePrices();
                Essence.getHandler().notificationManager.pushNotify("Значение установлено", Type.Info);
            }
        }
    }

    public Map.Entry<AutoBuyItemClass, Integer> getAliveItemClass(AutoBuyItemClass item) {
        int index = 0;

        for(AutoBuyItemClass iclass : this.list) {
            boolean itemCheck = iclass.item == item.item;
            boolean dataCheck = false;
            if (ClientHelper.isConnectedToServer("spookytime")) {
                dataCheck = item.spookyItemType == null && iclass.spookyItemType == null || iclass.spookyItemType != null && iclass.spookyItemType.equals(item.spookyItemType);
            } else {
                dataCheck = item.attributes == null && (iclass.attributes == null || iclass.attributes.isEmpty()) || iclass.attributes != null && iclass.attributes.equals(item.attributes);
            }

            if (itemCheck && dataCheck) {
                return Map.entry(iclass, index);
            }

            ++index;
        }

        return null;
    }

    public AutoBuyItemClass isNeedToBuyEnchanted(ItemStack stack) {
        if (stack.getItem() != Items.ENCHANTED_BOOK && !stack.isEnchanted()) {
            return null;
        }

        if (stack.getItem() == Items.ENCHANTED_BOOK) {
            Map<Enchantment, Integer> bookEnchants = EnchantmentHelper.getEnchantments(stack);
            if (bookEnchants.isEmpty()) {
                return null;
            }
            for(AutoBuyItemClass registeredItem : this.list) {
                if (registeredItem.item == Items.ENCHANTED_BOOK && registeredItem.enchants != null && !registeredItem.enchants.isEmpty()) {
                    boolean allMatch = true;

                    for(Enchant required : registeredItem.enchants) {
                        boolean hasEnchant = false;

                        for(Map.Entry<Enchantment, Integer> existing : bookEnchants.entrySet()) {
                            if (existing.getKey() == required.enchantment && (Integer)existing.getValue() >= required.level) {
                                hasEnchant = true;
                                break;
                            }
                        }

                        if (!hasEnchant) {
                            allMatch = false;
                            break;
                        }
                    }

                    if (allMatch) {
                        int totalPrice = AutoBuyUtil.getPrice(stack);
                        if (totalPrice == -1) continue;
                        int pricePerItem = totalPrice / stack.getCount();
                        if (registeredItem.buyPrice >= pricePerItem) {
                            return registeredItem;
                        }
                    }
                }
            }
            return null;
        }
        
        Map<Enchantment, Integer> itemEnchants = EnchantmentHelper.getEnchantments(stack);
        if (itemEnchants.containsKey(Enchantments.THORNS)) return null;
        
        for(AutoBuyItemClass registeredItem : this.list) {
            if (registeredItem.item != stack.getItem()) continue;
            if (registeredItem.enchants == null || registeredItem.enchants.isEmpty()) continue;
            
            boolean allMatch = true;
            for(Enchant required : registeredItem.enchants) {
                boolean hasEnchant = false;
                for(Map.Entry<Enchantment, Integer> existing : itemEnchants.entrySet()) {
                    if (existing.getKey() == required.enchantment && (Integer)existing.getValue() >= required.level) {
                        hasEnchant = true;
                        break;
                    }
                }
                if (!hasEnchant) {
                    allMatch = false;
                    break;
                }
            }
            if (allMatch) {
                int totalPrice = AutoBuyUtil.getPrice(stack);
                if (totalPrice == -1) continue;
                int pricePerItem = totalPrice / stack.getCount();
                if (registeredItem.buyPrice >= pricePerItem) {
                    return registeredItem;
                }
            }
        }
        return null;
    }

    public AutoBuyItemClass isNeedToBuyPotion(ItemStack stack) {
        List<EffectInstance> itemEffects = PotionUtils.getFullEffectsFromItem(stack);

        for(AutoBuyItemClass registeredItem : this.list) {
            if (registeredItem.potionEffects != null && !registeredItem.potionEffects.isEmpty() && registeredItem.item == stack.getItem() && itemEffects.size() == registeredItem.potionEffects.size()) {
                boolean allEffectsMatch = true;

                for(PotionEffectMatcher requiredEffect : registeredItem.potionEffects) {
                    boolean foundMatch = false;

                    for(EffectInstance itemEffect : itemEffects) {
                        int id = Effect.getId(itemEffect.getPotion());
                        int amplifier = itemEffect.getAmplifier();
                        int duration = itemEffect.getDuration() / 20;
                        if (id == requiredEffect.id && amplifier == requiredEffect.amplifier) {
                            if (requiredEffect.duration == -1) {
                                foundMatch = true;
                                break;
                            }

                            if (duration == requiredEffect.duration) {
                                foundMatch = true;
                                break;
                            }
                        }
                    }

                    if (!foundMatch) {
                        allEffectsMatch = false;
                        break;
                    }
                }

                if (allEffectsMatch) {
                    int totalPrice = AutoBuyUtil.getPrice(stack);
                    if (totalPrice == -1) continue;
                    int pricePerItem = totalPrice / stack.getCount();
                    if (registeredItem.buyPrice >= pricePerItem) {
                        return registeredItem;
                    }
                }
            }
        }

        return null;
    }

    public void clear() {
        this.list.clear();
        this.addItems();
        this.rebuildMap();
    }

    public AutoBuyItemClass isNeedToBuy(ItemStack stack) {
        for(AutoBuyItemClass registeredItem : getCandidates(stack)) {
            if ((registeredItem.potionEffects == null || registeredItem.potionEffects.isEmpty()) && (registeredItem.attributes == null || registeredItem.attributes.isEmpty()) && (registeredItem.spookyItemType == null || registeredItem.spookyItemType.isEmpty()) && (registeredItem.enchants == null || registeredItem.enchants.isEmpty())) {
                int totalPrice = AutoBuyUtil.getPrice(stack);
                if (totalPrice == -1) continue;
                int pricePerItem = totalPrice / stack.getCount();
                if (registeredItem.buyPrice >= pricePerItem) {
                    return registeredItem;
                }
            }
        }

        return null;
    }

    public AutoBuyItemClass isNeedToBuy(ItemStack stack, String spookyItemType) {
        if (spookyItemType == null) {
            return this.isNeedToBuy(stack);
        } else {
            for(AutoBuyItemClass registeredItem : getCandidates(stack)) {
                if (registeredItem.spookyItemType != null && spookyItemType.endsWith(registeredItem.spookyItemType)) {
                    int totalPrice = AutoBuyUtil.getPrice(stack);
                    if (totalPrice == -1) continue;
                    int pricePerItem = totalPrice / stack.getCount();
                    if (registeredItem.buyPrice >= pricePerItem) {
                        return registeredItem;
                    }
                }
            }

            return null;
        }
    }

    public AutoBuyItemClass isNeedToBuy(ItemStack stack, HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> attributes) {
        for(AutoBuyItemClass registeredItem : getCandidates(stack)) {
            if (registeredItem.attributes != null && registeredItem.attributes.equals(attributes)) {
                int totalPrice = AutoBuyUtil.getPrice(stack);
                if (totalPrice == -1) continue;
                int pricePerItem = totalPrice / stack.getCount();
                if (registeredItem.buyPrice >= pricePerItem) {
                    return registeredItem;
                }
            }
        }

        return null;
    }
}
