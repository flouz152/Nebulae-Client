package beame.components.modules.misc.AutoBuyLogic.Items;

import beame.components.modules.misc.AutoBuyLogic.AutoBuyUtil;
import beame.util.animation.AnimationMath;
import beame.util.chat.ColorFormatter;
import beame.util.color.ColorUtils;
import beame.util.fonts.Fonts;
import beame.util.render.ClientHandler;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;
import net.minecraft.potion.PotionUtils;

import java.util.*;

import static beame.util.IMinecraft.mc;
import static beame.util.ClientHelper.isConnectedToServer;

public class AutoBuyItemClass {
// leaked by itskekoff; discord.gg/sk3d mZLqTdp6
    public String displayName = "";
    public String texture = "";
    public String spookyItemType;
    public int buyPrice = 0;
    public Item item = Items.AIR;
    public HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> attributes = new HashMap<>();
    public float hoverAnimation = 0;
    public float selectAnimation = 0;
    public float priceAnimation = 0;
    public float color1Animation = 0;
    public float color2Animation = 0;
    public boolean isParsingEnabled = false; // Флаг для включения парсинга цен
    public List<Enchant> enchants;
    public List<PotionEffectMatcher> potionEffects;

    public AutoBuyItemClass(String displayName, int buyPrice, Item item, HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> attributes, String spookyType, String texture, List<Enchant> enchants, List<PotionEffectMatcher> potionEffects) {
        this.displayName = displayName != null ? displayName : "";
        this.buyPrice = buyPrice;
        this.item = item;
        this.attributes = attributes != null ? attributes : new HashMap<>();
        this.spookyItemType = spookyType;
        this.texture = texture != null ? texture : "";
        this.enchants = enchants != null ? enchants : new ArrayList<>();
        this.potionEffects = potionEffects != null ? potionEffects : new ArrayList<>();
    }

    public AutoBuyItemClass(String displayName, int buyPrice, Item item, HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> attributes, String spookyType, String texture) {
        this(displayName, buyPrice, item, attributes, spookyType, texture, new ArrayList<>(), new ArrayList<>());
    }

    public AutoBuyItemClass(String displayName, int buyPrice, Item item, List<PotionEffectMatcher> potionEffects) {
        this(displayName, buyPrice, item, new HashMap<>(), null, "", new ArrayList<>(), potionEffects);
    }

    public AutoBuyItemClass(String displayName, int buyPrice, Item item, String spookyType, String texture) {
        this(displayName, buyPrice, item, new HashMap<>(), spookyType, texture, new ArrayList<>(), new ArrayList<>());
    }

    public AutoBuyItemClass(String displayName, int buyPrice, Item item, HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> attributes) {
        this(displayName, buyPrice, item, attributes, null, "", new ArrayList<>(), new ArrayList<>());
    }

    public AutoBuyItemClass(String displayName, int buyPrice, Item item) {
        this(displayName, buyPrice, item, new HashMap<>(), null, "", new ArrayList<>(), new ArrayList<>());
    }

    public AutoBuyItemClass(int buyPrice, Item item) {
        this("", buyPrice, item, new HashMap<>(), null, "", new ArrayList<>(), new ArrayList<>());
    }

    public AutoBuyItemClass(int buyPrice, Item item, HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> attributes) {
        this("", buyPrice, item, attributes, null, "", new ArrayList<>(), new ArrayList<>());
    }

    public AutoBuyItemClass(int buyPrice, Item item, String spookyType, String texture) {
        this("", buyPrice, item, new HashMap<>(), spookyType, texture, new ArrayList<>(), new ArrayList<>());
    }

    public AutoBuyItemClass(String displayName, int buyPrice, Item item, List<Enchant> enchants, String spookyItemType) {
        this(displayName, buyPrice, item, new HashMap<>(), spookyItemType, null, enchants, new ArrayList<>());
    }

    public AutoBuyItemClass(String displayName, int buyPrice, Item item, List<Enchant> enchants, String spookyItemType, String texture) {
        this(displayName, buyPrice, item, new HashMap<>(), spookyItemType, texture, enchants, new ArrayList<>());
    }


    public void render(float x, float y, int mouseX, int mouseY) {
        ItemStack stack = item.getDefaultInstance();
        if (!attributes.isEmpty()) {
            stack.addEnchantment(Enchantments.AQUA_AFFINITY, 1);
        }
        stack.setDisplayName(ITextComponent.getTextComponentOrEmpty(displayName));
        if (!Objects.equals(texture, "")) {
            try {
                stack.setTag(JsonToNBT.getTagFromJson(String.format("{SkullOwner:{Properties:{textures:[{Value:\"%s\"}]},Name:\"%s\"}}", texture, displayName)));
            } catch (Exception ignored) {
            }
        }

        boolean hovered = ClientHandler.isInRegion(mouseX, mouseY, (int) x - 2, (int) y - 2, 20, 20);
        hoverAnimation = AnimationMath.fast(hoverAnimation, hovered ? 1 : 0, 12);

        mc.getItemRenderer().renderItemIntoGUI(stack, (int) x, (int) y);

        RenderSystem.disableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.disableBlend();

        boolean donitem = displayName.contains("Талисман") || displayName.contains("Сфера");
        if (stack.getCount() > 1 || donitem) {
            boolean foundLvL = displayName.contains("1") || displayName.contains("2") || displayName.contains("3");
            int founded = foundLvL ? (displayName.contains("1") ? 1 : (displayName.contains("2") ? 2 : (displayName.contains("3") ? 3 : -1))) : 3;
            int count = donitem ? (foundLvL ? founded : 3) : stack.getCount();
        }

        String text = ColorFormatter.get("${blue}" + displayName + "${gray}" + (buyPrice == 0 ? " (${green}10$${gray})" : " (${red}До ${green}" + buyPrice + "$${gray})"));
        int info_color = ColorUtils.rgba(10, 10, 10, (int) (175 * hoverAnimation));
        GL11.glPushMatrix();
        float textWidth = Fonts.SF_BOLD.get(11).getWidth(text);
        AnimationMath.sizeAnimation(x + (20 / 2), y, hoverAnimation);
        ClientHandler.drawRound(x - (textWidth / 2) - 2.5f + (17 / 2), y - 10, textWidth + 7.5f, 10, 3, info_color);
        Fonts.SF_BOLD.get(11).drawString(text, x - (textWidth / 2) + (17 / 2), y - 5, ColorUtils.setAlpha(-1, (int) (255 * hoverAnimation)));
        GL11.glPopMatrix();

        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.enableBlend();
    }
}