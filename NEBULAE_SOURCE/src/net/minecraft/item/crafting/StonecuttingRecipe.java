package net.minecraft.item.crafting;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class StonecuttingRecipe extends SingleItemRecipe
{
// leaked by itskekoff; discord.gg/sk3d sZaV0aRE
    public StonecuttingRecipe(ResourceLocation id, String group, Ingredient ingredient, ItemStack result)
    {
        super(IRecipeType.STONECUTTING, IRecipeSerializer.STONECUTTING, id, group, ingredient, result);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(IInventory inv, World worldIn)
    {
        return this.ingredient.test(inv.getStackInSlot(0));
    }

    public ItemStack getIcon()
    {
        return new ItemStack(Blocks.STONECUTTER);
    }
}
