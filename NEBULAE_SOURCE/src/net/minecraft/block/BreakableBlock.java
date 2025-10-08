package net.minecraft.block;

import net.minecraft.util.Direction;

public class BreakableBlock extends Block
{
// leaked by itskekoff; discord.gg/sk3d s51Q4Frj
    protected BreakableBlock(AbstractBlock.Properties properties)
    {
        super(properties);
    }

    public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side)
    {
        return adjacentBlockState.isIn(this) ? true : super.isSideInvisible(state, adjacentBlockState, side);
    }
}
