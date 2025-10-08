package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;

public class OperatorOnlyItem extends BlockItem
{
// leaked by itskekoff; discord.gg/sk3d xnMny8Z6
    public OperatorOnlyItem(Block blockIn, Item.Properties builder)
    {
        super(blockIn, builder);
    }

    @Nullable
    protected BlockState getStateForPlacement(BlockItemUseContext context)
    {
        PlayerEntity playerentity = context.getPlayer();
        return playerentity != null && !playerentity.canUseCommandBlock() ? null : super.getStateForPlacement(context);
    }
}
