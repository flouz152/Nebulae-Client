package net.minecraft.enchantment;

import net.minecraft.util.WeightedRandom;

public class EnchantmentData extends WeightedRandom.Item
{
// leaked by itskekoff; discord.gg/sk3d sTWA6dqy
    public final Enchantment enchantment;
    public final int enchantmentLevel;

    public EnchantmentData(Enchantment enchantmentObj, int enchLevel)
    {
        super(enchantmentObj.getRarity().getWeight());
        this.enchantment = enchantmentObj;
        this.enchantmentLevel = enchLevel;
    }
}
