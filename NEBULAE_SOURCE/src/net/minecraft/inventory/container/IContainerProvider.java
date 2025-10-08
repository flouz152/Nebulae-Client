package net.minecraft.inventory.container;

import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

@FunctionalInterface
public interface IContainerProvider
{
// leaked by itskekoff; discord.gg/sk3d xvhHYHul
    @Nullable
    Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_);
}
