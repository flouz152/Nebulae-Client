package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class AquaAffinityEnchantment extends Enchantment
{
// leaked by itskekoff; discord.gg/sk3d GT93EZn5
    public AquaAffinityEnchantment(Enchantment.Rarity rarityIn, EquipmentSlotType... slots)
    {
        super(rarityIn, EnchantmentType.ARMOR_HEAD, slots);
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(int enchantmentLevel)
    {
        return 1;
    }

    public int getMaxEnchantability(int enchantmentLevel)
    {
        return this.getMinEnchantability(enchantmentLevel) + 40;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel()
    {
        return 1;
    }
}
