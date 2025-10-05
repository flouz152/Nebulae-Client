package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class BedItem extends BlockItem
{
// leaked by itskekoff; discord.gg/sk3d 4VCQq8zz
    public BedItem(Block blockIn, Item.Properties properties)
    {
        super(blockIn, properties);
    }

    protected boolean placeBlock(BlockItemUseContext context, BlockState state)
    {
        return context.getWorld().setBlockState(context.getPos(), state, 26);
    }
}
