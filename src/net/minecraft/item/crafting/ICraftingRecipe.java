package net.minecraft.item.crafting;

import net.minecraft.inventory.CraftingInventory;

public interface ICraftingRecipe extends IRecipe<CraftingInventory>
{
// leaked by itskekoff; discord.gg/sk3d 41EoM2eK
default IRecipeType<?> getType()
    {
        return IRecipeType.CRAFTING;
    }
}
