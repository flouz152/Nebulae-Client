package net.minecraft.item;

import net.minecraft.item.crafting.Ingredient;

public interface IItemTier
{
// leaked by itskekoff; discord.gg/sk3d 6LoQKkwi
    int getMaxUses();

    float getEfficiency();

    float getAttackDamage();

    int getHarvestLevel();

    int getEnchantability();

    Ingredient getRepairMaterial();
}
