package net.minecraft.client.gui.recipebook;

import java.util.List;
import net.minecraft.item.crafting.IRecipe;

public interface IRecipeUpdateListener
{
// leaked by itskekoff; discord.gg/sk3d CDma5LFl
    void recipesShown(List < IRecipe<? >> recipes);
}
